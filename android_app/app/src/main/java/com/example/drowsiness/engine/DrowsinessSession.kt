package com.example.drowsiness.engine

import com.example.drowsiness.acquisition.EcgDaq
import com.example.drowsiness.config.AndroidRuntimeConfig
import com.example.drowsiness.logging.RoomDecisionLog
import com.example.drowsiness.pipeline.FeaturePipeline
import com.example.drowsiness.pipeline.HrvFeatures
import com.example.drowsiness.pipeline.WindowResult
import kotlinx.coroutines.*

/**
 * Session orchestrator wiring DAQ → FeaturePipeline → DrowsinessEngine → RoomDecisionLog.
 *
 * Runs on a coroutine loop, reading frames from the DAQ, feeding them through
 * the feature pipeline, and passing completed windows to the engine for
 * inference and alert decisions. Every decision is persisted via [RoomDecisionLog].
 *
 * @param daq               Hardware or synthetic ECG source.
 * @param featurePipeline   On-device feature extraction pipeline.
 * @param model             Loaded TFLite or ONNX model.
 * @param runtimeConfig     Alert thresholds and timing from pipeline_config.yaml.
 * @param decisionLog       Room-backed decision logger.
 */
class DrowsinessSession(
    private val daq: EcgDaq,
    private val featurePipeline: FeaturePipeline,
    private val model: DrowsinessModel,
    private val runtimeConfig: AndroidRuntimeConfig,
    private val decisionLog: RoomDecisionLog,
) {
    private var job: Job? = null
    private var engine: DrowsinessEngine? = null

    /** Whether the session is currently running. */
    val isRunning: Boolean get() = job?.isActive == true

    /** Current alert state (null if not started). */
    val isAlertActive: Boolean? get() = engine?.isAlertActive

    /**
     * Start the acquisition → inference loop.
     *
     * @param scope    CoroutineScope to launch the session in.
     * @param onDecision Optional callback for each decision (e.g., UI update).
     */
    fun start(
        scope: CoroutineScope,
        onDecision: ((AlertDecision) -> Unit)? = null,
    ) {
        if (isRunning) return

        val config = RuntimeConfig(
            featureUpdateSeconds = runtimeConfig.featureUpdateSeconds,
            inferenceSeconds = runtimeConfig.inferenceSeconds,
            alertThreshold = runtimeConfig.alertThreshold,
            clearThreshold = runtimeConfig.clearThreshold,
            alertDebounceCount = runtimeConfig.alertDebounceCount,
            clearDebounceCount = runtimeConfig.clearDebounceCount,
        )

        val eng = DrowsinessEngine(model, config, LocalDecisionLog { decision ->
            // Route base decision through the enriched logger
            decisionLog.appendSimple(
                decision.timestampMs,
                decision.probability,
                decision.alertActive,
                decision.reason,
                config.alertThreshold,
            )
        })
        engine = eng

        job = scope.launch(Dispatchers.Default) {
            daq.start(hardwareTrigger = false)
            try {
                while (isActive) {
                    // Read a frame from the DAQ
                    val frame = withContext(Dispatchers.IO) {
                        daq.readSamples()
                    }

                    // Feed into the pipeline (may produce 0 or 1 window results)
                    val windowResult = featurePipeline.ingestFrame(frame)

                    if (windowResult != null) {
                        // We have a complete window — run inference
                        val features = HrvFeatures.featureVector(windowResult.features)
                        val decision = eng.onFeatureWindow(
                            features,
                            windowResult.valid,
                            System.currentTimeMillis(),
                        )

                        // Also log the enriched version with window context
                        decisionLog.append(
                            timestampMs = decision.timestampMs,
                            probability = decision.probability,
                            alertActive = decision.alertActive,
                            reason = decision.reason,
                            threshold = config.alertThreshold,
                            window = windowResult,
                        )

                        onDecision?.invoke(decision)

                        // Drain any additional windows from a large frame
                        var next: WindowResult?
                        do {
                            next = featurePipeline.tryExtractWindow()
                            if (next != null) {
                                val feat = HrvFeatures.featureVector(next.features)
                                val dec = eng.onFeatureWindow(
                                    feat,
                                    next.valid,
                                    System.currentTimeMillis(),
                                )
                                decisionLog.append(
                                    dec.timestampMs, dec.probability, dec.alertActive,
                                    dec.reason, config.alertThreshold, next,
                                )
                                onDecision?.invoke(dec)
                            }
                        } while (next != null)
                    }
                }
            } finally {
                daq.stop()
            }
        }
    }

    /** Stop the session. */
    fun stop() {
        job?.cancel()
        job = null
        engine?.reset()
    }
}
