package com.example.drowsiness.pipeline

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Contact-quality gate mirroring `signal_processing/contact_quality.py`.
 *
 * Rolling-window kurtosis + median template correlation determines whether
 * a window has sufficient skin-electrode contact for valid HRV computation.
 * Config keys: `quality.rolling_seconds`, `quality.min_kurtosis`,
 * `quality.max_kurtosis`, `quality.min_template_correlation`,
 * `hrv.minimum_valid_contact_seconds`.
 */
object ContactQuality {

    /**
     * Median correlation of each detected beat with the median beat template.
     * Mirrors `_median_template_correlation` in Python.
     */
    internal fun medianTemplateCorrelation(
        signal: DoubleArray,
        peaks: IntArray,
        fs: Double,
    ): Double {
        val half = maxOf(1, (0.10 * fs).toInt())
        val beats = peaks.filter { it - half >= 0 && it + half < signal.size }
            .map { p -> signal.sliceArray((p - half)..(p + half)) }
        if (beats.size < 3) return 0.0

        // Median template: element-wise median across beats
        val templateLen = 2 * half + 1
        val template = DoubleArray(templateLen) { col ->
            val column = beats.map { it[col] }.sorted()
            column[column.size / 2] // median
        }

        val normT = norm(template)
        if (normT == 0.0) return 0.0

        val scores = beats.mapNotNull { beat ->
            val normB = norm(beat)
            val denom = normB * normT
            if (denom > 0.0) abs(dot(beat, template) / denom) else null
        }.sorted()

        return if (scores.isNotEmpty()) scores[scores.size / 2] else 0.0
    }

    /**
     * Excess kurtosis (Fisher=false, bias=false) matching scipy.stats.kurtosis.
     * Returns the sample excess kurtosis which scipy calls "excess" with Fisher=False
     * giving the "regular" kurtosis, and bias=False applying the sample correction.
     *
     * scipy kurtosis(x, fisher=False, bias=False) returns:
     *   (n-1)/((n-2)*(n-3)) * ((n+1)*m4/m2^2 - 3*(n-1)) + 3
     * where m4 and m2 are the biased central moments.
     */
    internal fun kurtosis(data: DoubleArray): Double {
        val n = data.size
        if (n < 4) return Double.NaN
        val mean = data.average()
        var m2 = 0.0
        var m4 = 0.0
        for (x in data) {
            val d = x - mean
            val d2 = d * d
            m2 += d2
            m4 += d2 * d2
        }
        m2 /= n
        m4 /= n
        if (m2 == 0.0) return Double.NaN
        // Unbiased (bias=False) kurtosis with Fisher=False (not excess, regular kurtosis)
        val g2 = m4 / (m2 * m2) // biased kurtosis
        // Bias-corrected formula for sample kurtosis (Fisher=False, bias=False):
        // = ((n-1)/((n-2)*(n-3))) * ((n+1)*g2 - 3*(n-1)) + 3
        val nd = n.toDouble()
        return ((nd - 1) / ((nd - 2) * (nd - 3))) * ((nd + 1) * g2 - 3 * (nd - 1)) + 3.0
    }

    /**
     * Rolling contact-quality mask. Mirrors `contact_quality_mask()` in Python.
     */
    fun contactQualityMask(
        samples: DoubleArray,
        timestampsS: DoubleArray,
        config: Map<String, Any>,
        peakIndices: IntArray? = null,
    ): QualityResult {
        val q = configSection(config, "quality")

        if (samples.size != timestampsS.size || samples.size < 4) {
            return QualityResult(false, Double.NaN, 0.0,
                "samples/timestamps invalid or too short", booleanArrayOf(), 0.0)
        }
        // Check timestamps finite and strictly increasing
        for (i in 1 until timestampsS.size) {
            if (!timestampsS[i].isFinite() || !timestampsS[i - 1].isFinite() ||
                timestampsS[i] <= timestampsS[i - 1]
            ) {
                return QualityResult(false, Double.NaN, 0.0,
                    "timestamps must be finite and strictly increasing", booleanArrayOf(), 0.0)
            }
        }

        val rollingS = configDouble(q, "rolling_seconds", 10.0)
        val minK = configDouble(q, "min_kurtosis")
        val maxK = configDouble(q, "max_kurtosis")
        val minCorr = configDouble(q, "min_template_correlation")

        // Estimate fs from timestamps
        val diffs = DoubleArray(timestampsS.size - 1) { timestampsS[it + 1] - timestampsS[it] }
        val medianDt = diffs.sorted()[diffs.size / 2]
        val fs = 1.0 / medianDt

        val peaks = peakIndices ?: intArrayOf()
        val masks = mutableListOf<Boolean>()
        val kurtoses = mutableListOf<Double>()
        val correlations = mutableListOf<Double>()

        var start = timestampsS[0]
        while (start < timestampsS.last()) {
            val end = start + rollingS
            val indices = (0 until samples.size).filter {
                timestampsS[it] >= start && timestampsS[it] < end
            }
            if (indices.isEmpty()) { start += rollingS; continue }

            val part = indices.map { samples[it] }.toDoubleArray()
            val localPeaks = peaks.filter { p ->
                p >= indices.first() && p <= indices.last()
            }.map { p ->
                // Map global peak index to local index within part
                indices.indexOf(indices.minByOrNull { abs(it - p) } ?: 0)
            }.filter { it >= 0 }.toIntArray()

            val k = if (part.size > 3) kurtosis(part) else Double.NaN
            val corr = if (peakIndices != null) {
                medianTemplateCorrelation(part, localPeaks, fs)
            } else 1.0

            val passed = k.isFinite() && k in minK..maxK && corr >= minCorr
            masks.add(passed)
            kurtoses.add(k)
            correlations.add(corr)

            start += rollingS
        }

        val mask = masks.toBooleanArray()
        val allPassed = mask.isNotEmpty() && mask.all { it }
        val duration = if (allPassed) mask.size * rollingS else 0.0
        val minimum = configDouble(
            configSection(config, "hrv"), "minimum_valid_contact_seconds", rollingS
        )
        val passed = allPassed && duration >= minimum
        val reason = if (passed) "" else
            "rolling SQI failed or valid contact segment below minimum duration"

        val finiteK = kurtoses.filter { it.isFinite() }
        val medianK = if (finiteK.isNotEmpty()) finiteK.sorted()[finiteK.size / 2] else Double.NaN
        val finiteC = correlations.filter { it.isFinite() }
        val medianC = if (finiteC.isNotEmpty()) finiteC.sorted()[finiteC.size / 2] else 0.0

        return QualityResult(passed, medianK, medianC, reason, mask, duration)
    }

    /**
     * Compatibility wrapper matching Python's `assess_contact_quality()`.
     */
    fun assessContactQuality(
        signal: DoubleArray,
        peakIndices: IntArray,
        fs: Double,
        config: Map<String, Any>,
        timestampsS: DoubleArray? = null,
    ): QualityResult {
        val ts = timestampsS ?: DoubleArray(signal.size) { it.toDouble() / fs }
        return contactQualityMask(signal, ts, config, peakIndices)
    }

    // --- Utility helpers ---

    private fun dot(a: DoubleArray, b: DoubleArray): Double {
        var sum = 0.0
        for (i in a.indices) sum += a[i] * b[i]
        return sum
    }

    private fun norm(a: DoubleArray): Double {
        var sum = 0.0
        for (x in a) sum += x * x
        return sqrt(sum)
    }
}
