package com.example.drowsiness.engine;

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
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ&\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0016\b\u0002\u0010\u001a\u001a\u0010\u0012\u0004\u0012\u00020\u001c\u0012\u0004\u0012\u00020\u0017\u0018\u00010\u001bJ\u0006\u0010\u001d\u001a\u00020\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u00108F\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\u0011R\u0011\u0010\u0012\u001a\u00020\u00108F\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001e"}, d2 = {"Lcom/example/drowsiness/engine/DrowsinessSession;", "", "daq", "Lcom/example/drowsiness/acquisition/EcgDaq;", "featurePipeline", "Lcom/example/drowsiness/pipeline/FeaturePipeline;", "model", "Lcom/example/drowsiness/engine/DrowsinessModel;", "runtimeConfig", "Lcom/example/drowsiness/config/AndroidRuntimeConfig;", "decisionLog", "Lcom/example/drowsiness/logging/RoomDecisionLog;", "(Lcom/example/drowsiness/acquisition/EcgDaq;Lcom/example/drowsiness/pipeline/FeaturePipeline;Lcom/example/drowsiness/engine/DrowsinessModel;Lcom/example/drowsiness/config/AndroidRuntimeConfig;Lcom/example/drowsiness/logging/RoomDecisionLog;)V", "engine", "Lcom/example/drowsiness/engine/DrowsinessEngine;", "isAlertActive", "", "()Ljava/lang/Boolean;", "isRunning", "()Z", "job", "Lkotlinx/coroutines/Job;", "start", "", "scope", "Lkotlinx/coroutines/CoroutineScope;", "onDecision", "Lkotlin/Function1;", "Lcom/example/drowsiness/engine/AlertDecision;", "stop", "app_debug"})
public final class DrowsinessSession {
    @org.jetbrains.annotations.NotNull()
    private final com.example.drowsiness.acquisition.EcgDaq daq = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.drowsiness.pipeline.FeaturePipeline featurePipeline = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.drowsiness.engine.DrowsinessModel model = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.drowsiness.config.AndroidRuntimeConfig runtimeConfig = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.drowsiness.logging.RoomDecisionLog decisionLog = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job job;
    @org.jetbrains.annotations.Nullable()
    private com.example.drowsiness.engine.DrowsinessEngine engine;
    
    public DrowsinessSession(@org.jetbrains.annotations.NotNull()
    com.example.drowsiness.acquisition.EcgDaq daq, @org.jetbrains.annotations.NotNull()
    com.example.drowsiness.pipeline.FeaturePipeline featurePipeline, @org.jetbrains.annotations.NotNull()
    com.example.drowsiness.engine.DrowsinessModel model, @org.jetbrains.annotations.NotNull()
    com.example.drowsiness.config.AndroidRuntimeConfig runtimeConfig, @org.jetbrains.annotations.NotNull()
    com.example.drowsiness.logging.RoomDecisionLog decisionLog) {
        super();
    }
    
    public final boolean isRunning() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Boolean isAlertActive() {
        return null;
    }
    
    /**
     * Start the acquisition → inference loop.
     *
     * @param scope    CoroutineScope to launch the session in.
     * @param onDecision Optional callback for each decision (e.g., UI update).
     */
    public final void start(@org.jetbrains.annotations.NotNull()
    kotlinx.coroutines.CoroutineScope scope, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super com.example.drowsiness.engine.AlertDecision, kotlin.Unit> onDecision) {
    }
    
    /**
     * Stop the session.
     */
    public final void stop() {
    }
}