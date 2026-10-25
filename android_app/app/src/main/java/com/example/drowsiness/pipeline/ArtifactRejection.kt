package com.example.drowsiness.pipeline

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Motion-artifact rejection mirroring `signal_processing/artifact_rejection.py`.
 *
 * Supports LMS and Kalman filter methods. Only processes quality-gated ECG;
 * refuses if the contact-quality gate failed.
 */
object ArtifactRejection {

    /**
     * Build a scalar noise reference from IMU samples.
     * Mirrors `IMUNoiseReference.reference()` in Python.
     *
     * @param imu Flat array: n scalars, (n,3) accel, or (n,6) accel+gyro (row-major).
     * @param columnsPerSample 1, 3, or 6.
     */
    fun imuNoiseReference(imu: DoubleArray, columnsPerSample: Int): DoubleArray {
        return when (columnsPerSample) {
            1 -> imu.copyOf()
            3 -> {
                val n = imu.size / 3
                DoubleArray(n) { i ->
                    val ax = imu[i * 3]
                    val ay = imu[i * 3 + 1]
                    val az = imu[i * 3 + 2]
                    sqrt(ax * ax + ay * ay + az * az)
                }
            }
            6 -> {
                val n = imu.size / 6
                val accelNorm = DoubleArray(n) { i ->
                    val ax = imu[i * 6]; val ay = imu[i * 6 + 1]; val az = imu[i * 6 + 2]
                    sqrt(ax * ax + ay * ay + az * az)
                }
                val gyroNorm = DoubleArray(n) { i ->
                    val gx = imu[i * 6 + 3]; val gy = imu[i * 6 + 4]; val gz = imu[i * 6 + 5]
                    sqrt(gx * gx + gy * gy + gz * gz)
                }
                val medA = median(accelNorm); val stdA = std(accelNorm) + 1e-8
                val medG = median(gyroNorm); val stdG = std(gyroNorm) + 1e-8
                DoubleArray(n) { i ->
                    (accelNorm[i] - medA) / stdA + (gyroNorm[i] - medG) / stdG
                }
            }
            else -> throw IllegalArgumentException(
                "IMU must be (n,), (n,3) accelerometer, or (n,6) accel+gyro"
            )
        }
    }

    /**
     * Normalized LMS adaptive filter. Mirrors `lms_filter()` in Python.
     */
    fun lmsFilter(
        ecg: DoubleArray,
        noiseReference: DoubleArray,
        stepSize: Double = 0.01,
        taps: Int = 8,
    ): DoubleArray {
        require(ecg.size == noiseReference.size) {
            "ECG and noise reference must be synchronized and equal length"
        }
        val w = DoubleArray(taps)
        val out = DoubleArray(ecg.size)
        val padded = DoubleArray(taps - 1 + noiseReference.size)
        noiseReference.copyInto(padded, taps - 1)

        for (i in ecg.indices) {
            // v = padded[i:i+taps][::-1]  — reversed window
            val v = DoubleArray(taps) { j -> padded[i + taps - 1 - j] }
            var dotWV = 0.0
            for (j in 0 until taps) dotWV += w[j] * v[j]
            val error = ecg[i] - dotWV
            var dotVV = 0.0
            for (j in 0 until taps) dotVV += v[j] * v[j]
            val norm = 1e-8 + dotVV
            for (j in 0 until taps) {
                w[j] += stepSize * error * v[j] / norm
            }
            out[i] = error
        }
        return out
    }

    /**
     * Kalman filter. Mirrors `kalman_filter()` in Python.
     */
    fun kalmanFilter(
        ecg: DoubleArray,
        noiseReference: DoubleArray,
        processNoise: Double = 1e-4,
        measurementNoise: Double = 1e-2,
    ): DoubleArray {
        require(ecg.size == noiseReference.size) {
            "ECG and noise reference must be synchronized and equal length"
        }
        val scale = std(ecg) / (std(noiseReference) + 1e-8)
        var state = 0.0
        var covariance = 1.0
        val out = DoubleArray(ecg.size)

        for (i in ecg.indices) {
            covariance += processNoise * (1 + abs(noiseReference[i]))
            val gain = covariance / (covariance + measurementNoise)
            state += gain * (noiseReference[i] * scale - state)
            covariance *= (1 - gain)
            out[i] = ecg[i] - state
        }
        return out
    }

    /**
     * Top-level artifact rejection. Mirrors `reject_motion_artifact()` in Python.
     * Refuses to process ECG that failed the contact-quality gate.
     */
    fun rejectMotionArtifact(
        gatedEcg: DoubleArray,
        imu: DoubleArray?,
        cfg: Map<String, Any>,
        quality: QualityResult? = null,
        imuColumns: Int = 1,
    ): DoubleArray {
        if (quality != null && !quality.passed) {
            throw IllegalArgumentException("refusing artifact rejection: ECG failed contact-quality gate")
        }

        val fCfg = configSection(cfg, "filter")
        val enabled = configBool(fCfg, "enabled", true)
        if (!enabled || imu == null) return gatedEcg.copyOf()

        val reference = imuNoiseReference(imu, imuColumns)
        val method = configString(fCfg, "method", "lms")

        return when (method) {
            "lms" -> lmsFilter(
                gatedEcg, reference,
                configDouble(fCfg, "lms_step_size", 0.01),
                configInt(fCfg, "taps", 8)
            )
            "kalman" -> kalmanFilter(gatedEcg, reference)
            else -> throw IllegalArgumentException("filter.method must be 'lms' or 'kalman'")
        }
    }

    private fun std(data: DoubleArray): Double {
        if (data.isEmpty()) return 0.0
        val mean = data.average()
        var sum = 0.0
        for (x in data) { val d = x - mean; sum += d * d }
        return sqrt(sum / data.size)
    }
}
