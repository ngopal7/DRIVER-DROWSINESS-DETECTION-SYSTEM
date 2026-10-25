package com.example.drowsiness.pipeline

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * HRV feature computation mirroring `signal_processing/hrv_features.py`.
 *
 * Only invoke after a passed continuous-contact gate.
 * Features: rmssd_ms, sdnn_ms, pnn50, n_rr (and reserved lf_hf).
 */
object HrvFeatures {

    /** Feature names in canonical order, matching Python DEFAULT_FEATURES. */
    val FEATURE_NAMES = listOf("rmssd_ms", "sdnn_ms", "pnn50")

    /**
     * Compute HRV features from R-peak indices.
     * Mirrors `compute_hrv_features()` in Python.
     *
     * @param peaks  Sorted R-peak sample indices.
     * @param fs     Sampling rate in Hz.
     * @param cfg    Pipeline config (uses `hrv.include_lf_hf`).
     * @return Map with keys: rmssd_ms, sdnn_ms, pnn50, n_rr (and lf_hf if enabled).
     */
    fun computeHrvFeatures(
        peaks: IntArray,
        fs: Double,
        cfg: Map<String, Any>,
    ): Map<String, Double> {
        // RR intervals in milliseconds
        val rrMs = if (peaks.size >= 2) {
            DoubleArray(peaks.size - 1) { i ->
                (peaks[i + 1] - peaks[i]) * 1000.0 / fs
            }
        } else doubleArrayOf()

        if (rrMs.size < 2) {
            return buildMap {
                put("rmssd_ms", Double.NaN)
                put("sdnn_ms", Double.NaN)
                put("pnn50", Double.NaN)
                put("n_rr", rrMs.size.toDouble())
            }
        }

        // Successive differences
        val d = DoubleArray(rrMs.size - 1) { i -> rrMs[i + 1] - rrMs[i] }

        // RMSSD: sqrt(mean(d^2))
        var sumSq = 0.0
        for (v in d) sumSq += v * v
        val rmssd = sqrt(sumSq / d.size)

        // SDNN: std(rr, ddof=1) — sample standard deviation
        val mean = rrMs.average()
        var varSum = 0.0
        for (v in rrMs) { val diff = v - mean; varSum += diff * diff }
        val sdnn = sqrt(varSum / (rrMs.size - 1))

        // pNN50: proportion of successive differences > 50 ms
        val pnn50 = d.count { abs(it) > 50.0 }.toDouble() / d.size

        val values = mutableMapOf(
            "rmssd_ms" to rmssd,
            "sdnn_ms" to sdnn,
            "pnn50" to pnn50,
            "n_rr" to rrMs.size.toDouble(),
        )

        // Reserved LF/HF slot
        val hrvCfg = configSection(cfg, "hrv")
        if (configBool(hrvCfg, "include_lf_hf", false)) {
            values["lf_hf"] = Double.NaN // requires validated spectral preprocessing
        }

        return values
    }

    /**
     * Convert feature map to ordered FloatArray for model inference.
     * Order matches [FEATURE_NAMES].
     */
    fun featureVector(features: Map<String, Double>): FloatArray {
        return FloatArray(FEATURE_NAMES.size) { i ->
            features[FEATURE_NAMES[i]]?.toFloat() ?: Float.NaN
        }
    }
}
