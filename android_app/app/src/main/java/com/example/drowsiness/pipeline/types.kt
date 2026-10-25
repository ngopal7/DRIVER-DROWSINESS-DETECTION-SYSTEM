package com.example.drowsiness.pipeline

/**
 * Data types mirroring `signal_processing/types.py` for cross-platform parity.
 *
 * Field names use Kotlin conventions but map 1:1 to the Python dataclasses.
 */

/** ECG acquisition channel. Maps to Python `ECGChannel` enum. */
enum class EcgChannel(val value: String) {
    STEERING_WHEEL("steering_wheel"),
    SEATBELT("seatbelt");

    companion object {
        fun fromValue(v: String): EcgChannel? = entries.firstOrNull { it.value == v }
    }
}

/**
 * Contact-quality assessment for a single channel window.
 * Maps to Python `QualityResult`.
 */
data class QualityResult(
    val passed: Boolean,
    val kurtosis: Double,
    val templateCorrelation: Double,
    val reason: String = "",
    val segmentMask: BooleanArray? = null,
    val validDurationSeconds: Double = 0.0,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QualityResult) return false
        return passed == other.passed &&
            kurtosis == other.kurtosis &&
            templateCorrelation == other.templateCorrelation &&
            reason == other.reason &&
            segmentMask.contentEquals(other.segmentMask) &&
            validDurationSeconds == other.validDurationSeconds
    }

    override fun hashCode(): Int {
        var result = passed.hashCode()
        result = 31 * result + kurtosis.hashCode()
        result = 31 * result + templateCorrelation.hashCode()
        result = 31 * result + reason.hashCode()
        result = 31 * result + (segmentMask?.contentHashCode() ?: 0)
        result = 31 * result + validDurationSeconds.hashCode()
        return result
    }
}

/**
 * R-peak detection output for a single channel.
 * Maps to Python `PeakResult`.
 */
data class PeakResult(
    val indices: IntArray,
    val method: String,
    val fallbackUsed: Boolean = false,
    val confidence: DoubleArray? = null,
    val confidenceComponents: Map<String, DoubleArray> = emptyMap(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PeakResult) return false
        return indices.contentEquals(other.indices) &&
            method == other.method &&
            fallbackUsed == other.fallbackUsed &&
            confidence.contentEquals(other.confidence)
    }

    override fun hashCode(): Int {
        var result = indices.contentHashCode()
        result = 31 * result + method.hashCode()
        result = 31 * result + fallbackUsed.hashCode()
        result = 31 * result + (confidence?.contentHashCode() ?: 0)
        return result
    }
}

/**
 * One SQI-gated ECG channel's detected beats for fusion.
 * Maps to Python `ChannelSeries`.
 */
data class ChannelSeries(
    val channel: EcgChannel,
    val peaks: PeakResult,
    val quality: QualityResult,
    val fs: Double,
    val motionArtifactEstimate: DoubleArray? = null,
)

/**
 * RR intervals that never span a channel handoff or contact discontinuity.
 * Maps to Python `RRSeries`.
 */
data class RRSeries(
    val peakIndices: IntArray,
    val rrMs: DoubleArray,
    val beatSources: Array<String>,
    val valid: Boolean,
    val disagreement: Boolean = false,
    val disagreementMs: Double = Double.NaN,
    val handoffBoundaries: IntArray = intArrayOf(),
    val metadata: MutableMap<String, Any?> = mutableMapOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RRSeries) return false
        return peakIndices.contentEquals(other.peakIndices) &&
            valid == other.valid && disagreement == other.disagreement
    }

    override fun hashCode(): Int {
        var result = peakIndices.contentHashCode()
        result = 31 * result + valid.hashCode()
        result = 31 * result + disagreement.hashCode()
        return result
    }
}

/**
 * Final fusion result for a window.
 * Maps to Python `FusionResult`.
 */
data class FusionResult(
    val selectedChannel: EcgChannel?,
    val peakIndices: IntArray,
    val valid: Boolean,
    val disagreement: Boolean,
    val reason: String,
    val metadata: Map<String, Any?> = emptyMap(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FusionResult) return false
        return selectedChannel == other.selectedChannel &&
            peakIndices.contentEquals(other.peakIndices) &&
            valid == other.valid && disagreement == other.disagreement
    }

    override fun hashCode(): Int {
        var result = (selectedChannel?.hashCode() ?: 0)
        result = 31 * result + peakIndices.contentHashCode()
        result = 31 * result + valid.hashCode()
        result = 31 * result + disagreement.hashCode()
        return result
    }
}

/**
 * Complete window result carrying everything the decision logger needs.
 * Extends beyond Python's pipeline output to include metadata the
 * Android logging layer requires.
 */
data class WindowResult(
    val startS: Double,
    val endS: Double,
    val valid: Boolean,
    val continuousContact: Boolean,
    val selectedChannel: EcgChannel?,
    val fusionDisagreement: Boolean,
    val steeringQualityPassed: Boolean,
    val seatbeltQualityPassed: Boolean,
    val features: Map<String, Double>,
)
