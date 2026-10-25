package com.example.drowsiness.acquisition

import kotlin.math.PI
import kotlin.math.sin
import java.util.Random

/**
 * Deterministic synthetic ECG source for pipeline integration testing without hardware.
 * Mirrors `data_collection/synthetic_ecg.py`: sine-based QRS approximation + noise.
 *
 * @param fs               Sampling rate in Hz (must match pipeline_config.yaml).
 * @param frameSamples      Number of samples per [readSamples] call.
 * @param seed              Deterministic seed for reproducibility.
 * @param seatbeltScale     Amplitude scaling for the seatbelt channel.
 * @param seatbeltDriftPpm  Simulated clock drift in ppm for seatbelt.
 */
class SyntheticEcgDaq(
    private val fs: Double = 250.0,
    private val frameSamples: Int = 250,
    private val seed: Int = 7,
    private val seatbeltScale: Float = 0.65f,
    private val seatbeltDriftPpm: Float = 25f,
) : EcgDaq {

    private var running = false
    private var sampleIndex = 0L
    private lateinit var rng: java.util.Random

    override fun start(hardwareTrigger: Boolean) {
        rng = java.util.Random(seed.toLong())
        sampleIndex = 0
        running = true
    }

    override fun readSamples(): EcgFrame {
        check(running) { "SyntheticEcgDaq not started" }
        val n = frameSamples
        val steering = FloatArray(n)
        val seatbelt = FloatArray(n)
        val ground = FloatArray(n)
        val imu = FloatArray(n * 6) // accel + gyro
        val timestamps = DoubleArray(n)

        val heartRateHz = 1.2 // ~72 bpm
        val driftFactor = 1.0 + seatbeltDriftPpm / 1e6

        for (i in 0 until n) {
            val t = (sampleIndex + i) / fs
            val tDrifted = t * driftFactor
            timestamps[i] = t

            // Simplified synthetic QRS: sharp sine pulse at heart rate
            val phase = (t * heartRateHz * 2 * PI) % (2 * PI)
            val qrs = if (phase < 0.3) (sin(phase / 0.3 * PI) * 1.2).toFloat() else 0f
            val noise = (rng.nextGaussian() * 0.05).toFloat()

            steering[i] = qrs + noise

            // Seatbelt: scaled + drifted + extra noise
            val phaseSb = (tDrifted * heartRateHz * 2 * PI) % (2 * PI)
            val qrsSb = if (phaseSb < 0.3) (sin(phaseSb / 0.3 * PI) * 1.2).toFloat() else 0f
            seatbelt[i] = qrsSb * seatbeltScale + (rng.nextGaussian() * 0.08).toFloat()

            ground[i] = (rng.nextGaussian() * 0.01).toFloat()

            // IMU: near-stationary with small vibration
            val base = i * 6
            imu[base + 0] = (rng.nextGaussian() * 0.02).toFloat()       // ax
            imu[base + 1] = (rng.nextGaussian() * 0.02).toFloat()       // ay
            imu[base + 2] = (9.81f + (rng.nextGaussian() * 0.02).toFloat()) // az (gravity)
            imu[base + 3] = (rng.nextGaussian() * 0.005).toFloat()      // gx
            imu[base + 4] = (rng.nextGaussian() * 0.005).toFloat()      // gy
            imu[base + 5] = (rng.nextGaussian() * 0.005).toFloat()      // gz
        }

        sampleIndex += n
        return EcgFrame(steering, seatbelt, ground, imu, timestamps)
    }

    override fun stop() {
        running = false
    }
}
