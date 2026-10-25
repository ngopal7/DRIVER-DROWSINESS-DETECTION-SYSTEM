package com.example.drowsiness.engine

/**
 * Runtime-configurable on-device alert policy.
 *
 * Moved from root package to `engine/` sub-package. Model is supplied by a
 * TFLite/ONNX adapter; features are supplied by [OnDeviceFeaturePipeline].
 *
 * Config values come from `pipeline_config.yaml`'s `android` section —
 * never bake study thresholds into code.
 */

// --- Contracts (unchanged from original skeleton) ---

data class RuntimeConfig(
    val featureUpdateSeconds: Long,
    val inferenceSeconds: Long,
    val alertThreshold: Float,
    val clearThreshold: Float,
    val alertDebounceCount: Int,
    val clearDebounceCount: Int,
)

data class AlertDecision(
    val timestampMs: Long,
    val probability: Float,
    val alertActive: Boolean,
    val reason: String,
)

fun interface LocalDecisionLog {
    fun append(decision: AlertDecision)
}

fun interface DrowsinessModel {
    fun infer(features: FloatArray): Float
}

fun interface OnDeviceFeaturePipeline {
    fun update(ecgSteering: FloatArray, ecgSeatbelt: FloatArray, imu: FloatArray): Pair<FloatArray, Boolean>
}

// --- Engine ---

class DrowsinessEngine(
    private val model: DrowsinessModel,
    private val config: RuntimeConfig,
    private val log: LocalDecisionLog,
) {
    private var active = false
    private var above = 0
    private var below = 0

    /** Whether the alert is currently active. */
    val isAlertActive: Boolean get() = active

    fun onFeatureWindow(
        features: FloatArray,
        qualityPassed: Boolean,
        timestampMs: Long,
    ): AlertDecision {
        val probability = if (qualityPassed) model.infer(features) else Float.NaN
        val reason = when {
            !qualityPassed -> "quality_gate_failed"
            probability >= config.alertThreshold -> {
                above++; below = 0
                if (above >= config.alertDebounceCount) active = true
                "above_alert_threshold"
            }
            probability <= config.clearThreshold -> {
                below++; above = 0
                if (below >= config.clearDebounceCount) active = false
                "below_clear_threshold"
            }
            else -> {
                above = 0; below = 0; "hysteresis_band"
            }
        }
        return AlertDecision(timestampMs, probability, active, reason).also(log::append)
    }

    /** Reset debounce counters and alert state (e.g. on session restart). */
    fun reset() {
        active = false; above = 0; below = 0
    }
}
