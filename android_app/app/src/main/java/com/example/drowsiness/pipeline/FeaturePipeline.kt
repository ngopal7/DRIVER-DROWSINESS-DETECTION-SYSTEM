package com.example.drowsiness.pipeline

import com.example.drowsiness.acquisition.EcgFrame

/**
 * Full on-device feature extraction pipeline implementing [OnDeviceFeaturePipeline]
 * from the DrowsinessEngine contract.
 *
 * Orchestrates the same stage sequence as Python's `process_recording()`:
 *   contact quality → artifact rejection → R-peak detection → channel fusion → HRV features
 *
 * Manages a sliding window buffer configured via `hrv.window_seconds` and
 * `hrv.overlap_seconds`, matching the Python pipeline's windowing logic.
 */
class FeaturePipeline(
    private val config: Map<String, Any>,
) {
    private val fs: Double = configDouble(config, "sampling_rate_hz").also {
        require(it > 0) { "sampling_rate_hz must be positive" }
    }
    private val windowSamples: Int = (configDouble(configSection(config, "hrv"), "window_seconds") * fs).toInt()
    private val overlapSamples: Int = (configDouble(configSection(config, "hrv"), "overlap_seconds") * fs).toInt()
    private val stepSamples: Int = windowSamples - overlapSamples

    init {
        require(stepSamples > 0) { "overlap must be smaller than window length" }
    }

    // --- Ring buffers for accumulating samples ---
    private val steeringBuf = RingBuffer(windowSamples * 2)
    private val seatbeltBuf = RingBuffer(windowSamples * 2)
    private val groundBuf = RingBuffer(windowSamples * 2)
    private val imuBuf = mutableListOf<FloatArray>() // rows of per-sample IMU
    private val tsBuf = mutableListOf<Double>()
    private var imuCols = 6
    private var samplesAccumulated = 0
    private var samplesConsumed = 0 // how many samples have been consumed into windows

    /**
     * Ingest a frame from the DAQ. Returns null if not enough data for a window yet,
     * or a [WindowResult] when a full window is available.
     *
     * Callers should loop calling this until null to drain all available windows
     * from a large frame.
     */
    fun ingestFrame(frame: EcgFrame): WindowResult? {
        // Append frame data to buffers
        for (i in 0 until frame.sampleCount) {
            steeringBuf.write(frame.steering[i].toDouble())
            seatbeltBuf.write(frame.seatbelt[i].toDouble())
            groundBuf.write(frame.ground[i].toDouble())
            tsBuf.add(frame.timestampsS[i])

            val cols = frame.imuColumns
            if (cols > 0) {
                imuCols = cols
                val row = FloatArray(cols) { c -> frame.imu[i * cols + c] }
                imuBuf.add(row)
            }
        }
        samplesAccumulated += frame.sampleCount

        return tryExtractWindow()
    }

    /**
     * Try to extract the next window. Returns null if insufficient data.
     */
    fun tryExtractWindow(): WindowResult? {
        val available = samplesAccumulated - samplesConsumed
        if (available < windowSamples) return null

        // Extract window slices
        val offset = samplesConsumed
        val steering = DoubleArray(windowSamples) { steeringBuf.read(offset + it) }
        val seatbelt = DoubleArray(windowSamples) { seatbeltBuf.read(offset + it) }
        val ground = DoubleArray(windowSamples) { groundBuf.read(offset + it) }
        val timestamps = DoubleArray(windowSamples) { tsBuf[offset + it] }
        val imu = DoubleArray(windowSamples * imuCols) { idx ->
            val sample = idx / imuCols
            val col = idx % imuCols
            imuBuf[offset + sample][col].toDouble()
        }

        // Advance by step
        samplesConsumed += stepSamples

        return processWindow(steering, seatbelt, ground, imu, timestamps)
    }

    /**
     * Process a single window through the full pipeline.
     * Mirrors the body of Python's `process_recording()` for-loop.
     */
    private fun processWindow(
        steering: DoubleArray,
        seatbelt: DoubleArray,
        ground: DoubleArray,
        imu: DoubleArray,
        timestamps: DoubleArray,
    ): WindowResult {
        // Build scalar motion reference
        val motion = ArtifactRejection.imuNoiseReference(imu, imuCols)

        // Initial R-peak detection (before artifact rejection)
        val rawSP = RPeakDetection.detectRPeaks(steering, fs, config, motion)
        val rawBP = RPeakDetection.detectRPeaks(seatbelt, fs, config, motion)

        // Contact quality assessment
        val sq = ContactQuality.assessContactQuality(steering, rawSP.indices, fs, config, timestamps)
        val bq = ContactQuality.assessContactQuality(seatbelt, rawBP.indices, fs, config, timestamps)

        // Artifact rejection (only for passed-quality channels)
        val sf = if (sq.passed) {
            ArtifactRejection.rejectMotionArtifact(steering, imu, config, sq, imuCols)
        } else steering.copyOf()

        val bf = if (bq.passed) {
            ArtifactRejection.rejectMotionArtifact(seatbelt, imu, config, bq, imuCols)
        } else seatbelt.copyOf()

        // Re-detect peaks on filtered signal (if quality passed)
        val sp = if (sq.passed) RPeakDetection.detectRPeaks(sf, fs, config, motion) else rawSP
        val bp = if (bq.passed) RPeakDetection.detectRPeaks(bf, fs, config, motion) else rawBP

        // Channel fusion
        val fusion = ChannelFusion.fuseChannels(
            sf, bf, ground, sq, bq,
            sp.indices, bp.indices, fs, config,
            sp, bp, motion
        )

        // Continuous contact check (no handoff boundaries)
        @Suppress("UNCHECKED_CAST")
        val handoffs = fusion.metadata["handoff_boundaries"]
        val continuous = when (handoffs) {
            is IntArray -> handoffs.isEmpty()
            is List<*> -> handoffs.isEmpty()
            else -> true
        }
        val valid = fusion.valid && continuous

        // HRV features (only for valid windows)
        val features = if (valid) {
            HrvFeatures.computeHrvFeatures(fusion.peakIndices, fs, config)
        } else {
            HrvFeatures.computeHrvFeatures(intArrayOf(), fs, config)
        }

        return WindowResult(
            startS = timestamps.first(),
            endS = timestamps.last(),
            valid = valid,
            continuousContact = continuous,
            selectedChannel = fusion.selectedChannel,
            fusionDisagreement = fusion.disagreement,
            steeringQualityPassed = sq.passed,
            seatbeltQualityPassed = bq.passed,
            features = features,
        )
    }

    /**
     * Implements the [OnDeviceFeaturePipeline] contract:
     * returns (featureVector, qualityPassed).
     */
    fun update(
        ecgSteering: FloatArray,
        ecgSeatbelt: FloatArray,
        imuData: FloatArray,
    ): Pair<FloatArray, Boolean> {
        // Build a frame from raw arrays (timestamps synthesized from sample count)
        val n = ecgSteering.size
        val ts = DoubleArray(n) { (samplesAccumulated + it).toDouble() / fs }
        val frame = EcgFrame(ecgSteering, ecgSeatbelt, FloatArray(n), imuData, ts)
        val result = ingestFrame(frame)

        return if (result != null) {
            Pair(HrvFeatures.featureVector(result.features), result.valid)
        } else {
            // Not enough data for a window yet; return NaN features
            Pair(FloatArray(HrvFeatures.FEATURE_NAMES.size) { Float.NaN }, false)
        }
    }

    /** Reset all buffers. */
    fun reset() {
        steeringBuf.clear()
        seatbeltBuf.clear()
        groundBuf.clear()
        imuBuf.clear()
        tsBuf.clear()
        samplesAccumulated = 0
        samplesConsumed = 0
    }

    /**
     * Simple growable buffer with indexed read access.
     * For the expected window sizes (~75k samples at 250 Hz × 300s) this is adequate.
     */
    private class RingBuffer(initialCapacity: Int) {
        private val data = mutableListOf<Double>()

        fun write(value: Double) { data.add(value) }
        fun read(index: Int): Double = data[index]
        fun clear() { data.clear() }
    }
}
