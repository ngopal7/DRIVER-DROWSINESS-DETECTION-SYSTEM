package com.example.drowsiness.pipeline

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * R-peak detection mirroring `signal_processing/r_peak_detection.py`.
 *
 * Uses a pure-Kotlin discrete wavelet transform with hardcoded Daubechies-4
 * coefficients, avoiding native dependencies while preserving numerical parity.
 */
object RPeakDetection {

    // --- Daubechies-4 filter coefficients (identical to PyWavelets db4) ---
    private val DB4_DEC_LO = doubleArrayOf(
        -0.010597401784997278, 0.032883011666982945,
        0.030841381835986965, -0.18703481171888114,
        -0.02798376941698385, 0.6308807679295904,
        0.7148465705525415, 0.23037781330885523
    )
    private val DB4_DEC_HI = doubleArrayOf(
        -0.23037781330885523, 0.7148465705525415,
        -0.6308807679295904, -0.02798376941698385,
        0.18703481171888114, 0.030841381835986965,
        -0.032883011666982945, -0.010597401784997278
    )
    private val DB4_REC_LO = doubleArrayOf(
        0.23037781330885523, 0.7148465705525415,
        0.6308807679295904, -0.02798376941698385,
        -0.18703481171888114, 0.030841381835986965,
        0.032883011666982945, -0.010597401784997278
    )
    private val DB4_REC_HI = doubleArrayOf(
        -0.010597401784997278, -0.032883011666982945,
        0.030841381835986965, 0.18703481171888114,
        -0.02798376941698385, -0.6308807679295904,
        0.7148465705525415, -0.23037781330885523
    )
    private val DB4_DEC_LEN = DB4_DEC_LO.size

    /**
     * Detect R-peaks. Mirrors `detect_r_peaks()` in Python.
     */
    fun detectRPeaks(
        ecg: DoubleArray,
        fs: Double,
        cfg: Map<String, Any>,
        motionArtifactEstimate: DoubleArray? = null,
    ): PeakResult {
        val rCfg = configSection(cfg, "r_peaks")
        // wavelet = cfg["r_peaks"]["wavelet"] — we only support db4
        val waveletName = configString(rCfg, "wavelet", "db4")
        require(waveletName == "db4") { "Only db4 wavelet supported in Kotlin port; got $waveletName" }

        try {
            val maxLevel = dwtMaxLevel(ecg.size, DB4_DEC_LEN)
            val level = minOf(4, maxLevel)
            if (level < 1) return PeakResult(intArrayOf(), "dwt_too_short", false)

            val coeffs = wavedec(ecg, level)

            // Reconstruct only level-1 detail (coeffs[-2] in Python = index level-1 here)
            val isolated = Array(coeffs.size) { i ->
                if (i == coeffs.size - 2) coeffs[i].copyOf()
                else DoubleArray(coeffs[i].size)
            }
            val detail = waverec(isolated, level, ecg.size)

            // Energy: detail^2
            val energy = DoubleArray(ecg.size) { i ->
                if (i < detail.size) detail[i] * detail[i] else 0.0
            }

            val candidates = adaptivePeaks(energy, fs, cfg)

            // Align to local ECG maximum
            val radius = (0.06 * fs).toInt()
            val aligned = candidates.map { p ->
                val lo = max(0, p - radius)
                val hi = min(ecg.size - 1, p + radius)
                lo + ecg.sliceArray(lo..hi).indexOfMax()
            }.distinct().sorted().toIntArray()

            if (aligned.size >= 2) {
                val (confidence, components) = computeConfidence(
                    aligned, energy, motionArtifactEstimate, fs, cfg
                )
                return PeakResult(aligned, "dwt_$waveletName", false, confidence, components)
            }
        } catch (_: Exception) {
            // Fall through to Pan-Tompkins fallback
        }

        val allowFallback = configBool(rCfg, "allow_pan_tompkins_fallback", true)
        if (!allowFallback) return PeakResult(intArrayOf(), "dwt_failed", false)

        // Pan-Tompkins fallback: derivative energy
        val derivative = DoubleArray(ecg.size) { i ->
            if (i == 0) 0.0 else ecg[i] - ecg[i - 1]
        }
        val derivEnergy = DoubleArray(derivative.size) { derivative[it] * derivative[it] }
        val peaks = adaptivePeaks(derivEnergy, fs, cfg)

        val (confidence, components) = if (peaks.isNotEmpty()) {
            computeConfidence(peaks, derivEnergy, motionArtifactEstimate, fs, cfg)
        } else Pair(doubleArrayOf(), emptyMap<String, DoubleArray>())

        return PeakResult(peaks, "pan_tompkins_benchmark", true, confidence, components)
    }

    /**
     * Block-wise adaptive peak detection. Mirrors `_adaptive_peaks()` in Python.
     */
    internal fun adaptivePeaks(energy: DoubleArray, fs: Double, cfg: Map<String, Any>): IntArray {
        val rCfg = configSection(cfg, "r_peaks")
        val distance = max(1, (fs * configDouble(rCfg, "refractory_ms") / 1000).toInt())
        val madMultiplier = configDouble(rCfg, "threshold_mad_multiplier")

        val block = max((8 * fs).toInt(), 1)
        val candidates = mutableListOf<Int>()

        var start = 0
        while (start < energy.size) {
            val end = min(start + block, energy.size)
            val part = energy.sliceArray(start until end)
            val med = median(part)
            val absDevs = DoubleArray(part.size) { abs(part[it] - med) }
            val madVal = median(absDevs)
            val threshold = med + madMultiplier * madVal

            // Simple peak finding: local maxima above threshold with refractory distance
            val localPeaks = findPeaks(part, threshold, distance)
            for (p in localPeaks) {
                candidates.add(start + p)
            }
            start += block
        }
        return candidates.toIntArray()
    }

    /**
     * Per-beat confidence scoring. Mirrors `_confidence()` in Python.
     */
    internal fun computeConfidence(
        peaks: IntArray,
        waveletEnergy: DoubleArray,
        motion: DoubleArray?,
        fs: Double,
        cfg: Map<String, Any>,
    ): Pair<DoubleArray, Map<String, DoubleArray>> {
        val cCfg = (configSection(cfg, "r_peaks") as? Map<*, *>)
            ?.get("confidence") as? Map<*, *> ?: emptyMap<String, Any>()

        val w0 = (cCfg["wavelet_amplitude_weight"] as? Number)?.toDouble() ?: 0.5
        val w1 = (cCfg["rr_plausibility_weight"] as? Number)?.toDouble() ?: 0.3
        val w2 = (cCfg["motion_artifact_weight"] as? Number)?.toDouble() ?: 0.2
        var wSum = w0 + w1 + w2
        require(w0 >= 0 && w1 >= 0 && w2 >= 0 && wSum > 0) {
            "R-peak confidence weights must be non-negative with positive sum"
        }
        val weights = doubleArrayOf(w0 / wSum, w1 / wSum, w2 / wSum)

        // Amplitude score
        val amplitudeScore = DoubleArray(peaks.size)
        for (i in peaks.indices) {
            val p = peaks[i]
            val radius = max(1, (4 * fs).toInt())
            val lo = max(0, p - radius)
            val hi = min(waveletEnergy.size, p + radius)
            val local = waveletEnergy.sliceArray(lo until hi)
            val localMed = median(local)
            val localMad = median(DoubleArray(local.size) { abs(local[it] - localMed) })
            val floor = localMed + localMad + 1e-12
            val amp = waveletEnergy[p]
            amplitudeScore[i] = (amp / (amp + floor)).coerceIn(0.0, 1.0)
        }

        // RR plausibility score
        val rrScore = DoubleArray(peaks.size) { 1.0 }
        if (peaks.size > 1) {
            val rr = DoubleArray(peaks.size - 1) {
                (peaks[it + 1] - peaks[it]) * 1000.0 / fs
            }
            for (i in 1 until peaks.size) {
                val rrIdx = i - 1
                val lo = max(0, rrIdx - 4) // nearby = rr[max(0,i-5):min(len(rr),i+4)]
                val hi = min(rr.size, rrIdx + 5)
                val nearby = rr.sliceArray(lo until hi)
                val medRR = median(nearby)
                val value = rr[rrIdx]
                rrScore[i] = exp(-abs(value - medRR) / (0.20 * medRR + 1e-12))
            }
            if (peaks.size > 1) rrScore[0] = rrScore[1]
        }

        // Motion artifact agreement score
        val motionScore = if (motion == null) {
            DoubleArray(peaks.size) { 1.0 }
        } else {
            require(motion.size == waveletEnergy.size) {
                "motion-artifact estimate must align with ECG samples"
            }
            val absMotion = DoubleArray(motion.size) { abs(motion[it]) }
            val baseline = median(absMotion) +
                median(DoubleArray(absMotion.size) { abs(absMotion[it] - median(absMotion)) }) + 1e-12
            DoubleArray(peaks.size) { i ->
                exp(-abs(motion[peaks[i]]) / baseline)
            }
        }

        val combined = DoubleArray(peaks.size) { i ->
            (weights[0] * amplitudeScore[i] + weights[1] * rrScore[i] + weights[2] * motionScore[i])
                .coerceIn(0.0, 1.0)
        }

        val components = mapOf(
            "wavelet_amplitude" to amplitudeScore,
            "rr_plausibility" to rrScore,
            "motion_artifact_agreement" to motionScore,
        )
        return Pair(combined, components)
    }

    // --- Minimal DWT implementation ---

    /**
     * Maximum decomposition level for DWT. Mirrors `pywt.dwt_max_level`.
     */
    internal fun dwtMaxLevel(signalLen: Int, filterLen: Int): Int {
        var level = 0
        var len = signalLen
        while (len >= filterLen) {
            len = (len + filterLen - 1) / 2
            level++
        }
        return level
    }

    /**
     * Multi-level wavelet decomposition. Mirrors `pywt.wavedec`.
     * Returns [cA_n, cD_n, cD_n-1, ..., cD_1].
     */
    internal fun wavedec(signal: DoubleArray, level: Int): Array<DoubleArray> {
        val coeffs = mutableListOf<DoubleArray>()
        var approx = signal
        for (l in 0 until level) {
            val (cA, cD) = dwt(approx)
            coeffs.add(0, cD) // detail coefficients prepended
            approx = cA
        }
        coeffs.add(0, approx) // final approximation at the front
        return coeffs.toTypedArray()
    }

    /**
     * Multi-level wavelet reconstruction. Mirrors `pywt.waverec`.
     */
    internal fun waverec(coeffs: Array<DoubleArray>, level: Int, targetLen: Int): DoubleArray {
        var approx = coeffs[0]
        for (l in 0 until level) {
            val detail = coeffs[l + 1]
            approx = idwt(approx, detail)
        }
        // Trim to original signal length (pywt does [:len(ecg)])
        return if (approx.size > targetLen) approx.sliceArray(0 until targetLen) else approx
    }

    /**
     * Single-level DWT decomposition using symmetric (half-point) boundary extension.
     * pywt default mode is 'symmetric'.
     */
    private fun dwt(signal: DoubleArray): Pair<DoubleArray, DoubleArray> {
        val extended = symmetricExtend(signal, DB4_DEC_LEN)
        val outLen = (signal.size + DB4_DEC_LEN - 1) / 2
        val cA = DoubleArray(outLen)
        val cD = DoubleArray(outLen)
        for (i in 0 until outLen) {
            var sumA = 0.0
            var sumD = 0.0
            for (j in 0 until DB4_DEC_LEN) {
                val idx = 2 * i + j
                sumA += DB4_DEC_LO[j] * extended[idx]
                sumD += DB4_DEC_HI[j] * extended[idx]
            }
            cA[i] = sumA
            cD[i] = sumD
        }
        return Pair(cA, cD)
    }

    /**
     * Single-level inverse DWT reconstruction.
     */
    private fun idwt(cA: DoubleArray, cD: DoubleArray): DoubleArray {
        val outLen = 2 * cA.size
        val upA = DoubleArray(outLen) // upsampled
        val upD = DoubleArray(outLen)
        for (i in cA.indices) {
            upA[2 * i] = cA[i]
            upD[2 * i] = cD[i]
        }
        val padLen = DB4_REC_LO.size - 1
        val extA = symmetricExtend(upA, DB4_REC_LO.size)
        val extD = symmetricExtend(upD, DB4_REC_HI.size)
        val result = DoubleArray(outLen)
        for (i in 0 until outLen) {
            var sum = 0.0
            for (j in DB4_REC_LO.indices) {
                sum += DB4_REC_LO[j] * extA[i + j] + DB4_REC_HI[j] * extD[i + j]
            }
            result[i] = sum
        }
        return result
    }

    /**
     * Symmetric boundary extension (half-point symmetry) for convolution.
     */
    private fun symmetricExtend(signal: DoubleArray, filterLen: Int): DoubleArray {
        val pad = filterLen - 1
        val extended = DoubleArray(signal.size + 2 * pad)
        // Left padding: mirror
        for (i in 0 until pad) {
            extended[i] = signal[min(pad - 1 - i, signal.size - 1)]
        }
        // Center: original signal
        signal.copyInto(extended, pad)
        // Right padding: mirror
        for (i in 0 until pad) {
            extended[signal.size + pad + i] = signal[max(0, signal.size - 1 - i)]
        }
        return extended
    }

    // --- Helper functions ---

    internal fun findPeaks(
        data: DoubleArray,
        height: Double,
        distance: Int,
    ): List<Int> {
        val peaks = mutableListOf<Int>()
        for (i in 1 until data.size - 1) {
            if (data[i] > data[i - 1] && data[i] >= data[i + 1] && data[i] >= height) {
                peaks.add(i)
            }
        }
        // Enforce minimum distance (greedy, by amplitude)
        if (distance > 1 && peaks.size > 1) {
            val sorted = peaks.sortedByDescending { data[it] }
            val kept = mutableListOf<Int>()
            val removed = mutableSetOf<Int>()
            for (p in sorted) {
                if (p in removed) continue
                kept.add(p)
                for (other in peaks) {
                    if (other != p && abs(other - p) < distance) removed.add(other)
                }
            }
            return kept.sorted()
        }
        return peaks
    }

    private fun DoubleArray.indexOfMax(): Int {
        var maxIdx = 0
        for (i in 1 until size) {
            if (this[i] > this[maxIdx]) maxIdx = i
        }
        return maxIdx
    }
}
