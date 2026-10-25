package com.example.drowsiness.logging;

/**
 * Implements the `LocalDecisionLog` interface from `DrowsinessEngine.kt`,
 * backed by Room for persistent, queryable storage.
 *
 * Enriches the base [AlertDecision] with window quality flags from the
 * feature pipeline. Insertions happen on [Dispatchers.IO] to avoid
 * blocking the calling thread.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J8\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\f2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013J0\u0010\u0014\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/example/drowsiness/logging/RoomDecisionLog;", "", "dao", "Lcom/example/drowsiness/logging/DecisionDao;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "(Lcom/example/drowsiness/logging/DecisionDao;Lkotlinx/coroutines/CoroutineScope;)V", "append", "", "timestampMs", "", "probability", "", "alertActive", "", "reason", "", "threshold", "window", "Lcom/example/drowsiness/pipeline/WindowResult;", "appendSimple", "app_debug"})
public final class RoomDecisionLog {
    @org.jetbrains.annotations.NotNull()
    private final com.example.drowsiness.logging.DecisionDao dao = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    
    public RoomDecisionLog(@org.jetbrains.annotations.NotNull()
    com.example.drowsiness.logging.DecisionDao dao, @org.jetbrains.annotations.NotNull()
    kotlinx.coroutines.CoroutineScope scope) {
        super();
    }
    
    /**
     * Log a decision with full window context.
     *
     * @param timestampMs   System.currentTimeMillis() at decision time.
     * @param probability   Model output (NaN if quality gate failed).
     * @param alertActive   Whether the alert is currently active.
     * @param reason        Human-readable decision reason.
     * @param threshold     Snapshot of alert threshold from config.
     * @param window        Pipeline window result with quality flags and features.
     */
    public final void append(long timestampMs, float probability, boolean alertActive, @org.jetbrains.annotations.NotNull()
    java.lang.String reason, float threshold, @org.jetbrains.annotations.Nullable()
    com.example.drowsiness.pipeline.WindowResult window) {
    }
    
    /**
     * Simplified append matching the `LocalDecisionLog.append(AlertDecision)` signature
     * from the existing DrowsinessEngine interface. Window context is unavailable
     * through this path; fields default to NaN/false.
     */
    public final void appendSimple(long timestampMs, float probability, boolean alertActive, @org.jetbrains.annotations.NotNull()
    java.lang.String reason, float threshold) {
    }
}