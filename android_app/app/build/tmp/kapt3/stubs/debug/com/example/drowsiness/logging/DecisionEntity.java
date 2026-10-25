package com.example.drowsiness.logging;

/**
 * Room entity storing every alert decision with full window context.
 *
 * Every field the decision logger needs is persisted: probability, threshold snapshot,
 * per-channel quality flags, contact continuity, HRV features, and fusion metadata.
 * This enables offline audit of every decision the system made.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b0\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0089\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\u0006\u0012\u0006\u0010\f\u001a\u00020\b\u0012\u0006\u0010\r\u001a\u00020\b\u0012\u0006\u0010\u000e\u001a\u00020\b\u0012\u0006\u0010\u000f\u001a\u00020\u0010\u0012\u0006\u0010\u0011\u001a\u00020\u0010\u0012\u0006\u0010\u0012\u001a\u00020\u0010\u0012\u0006\u0010\u0013\u001a\u00020\u0010\u0012\u0006\u0010\u0014\u001a\u00020\u0010\u0012\b\u0010\u0015\u001a\u0004\u0018\u00010\n\u0012\u0006\u0010\u0016\u001a\u00020\b\u00a2\u0006\u0002\u0010\u0017J\t\u0010-\u001a\u00020\u0003H\u00c6\u0003J\t\u0010.\u001a\u00020\u0010H\u00c6\u0003J\t\u0010/\u001a\u00020\u0010H\u00c6\u0003J\t\u00100\u001a\u00020\u0010H\u00c6\u0003J\t\u00101\u001a\u00020\u0010H\u00c6\u0003J\t\u00102\u001a\u00020\u0010H\u00c6\u0003J\u000b\u00103\u001a\u0004\u0018\u00010\nH\u00c6\u0003J\t\u00104\u001a\u00020\bH\u00c6\u0003J\t\u00105\u001a\u00020\u0003H\u00c6\u0003J\t\u00106\u001a\u00020\u0006H\u00c6\u0003J\t\u00107\u001a\u00020\bH\u00c6\u0003J\t\u00108\u001a\u00020\nH\u00c6\u0003J\t\u00109\u001a\u00020\u0006H\u00c6\u0003J\t\u0010:\u001a\u00020\bH\u00c6\u0003J\t\u0010;\u001a\u00020\bH\u00c6\u0003J\t\u0010<\u001a\u00020\bH\u00c6\u0003J\u00ab\u0001\u0010=\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\b2\b\b\u0002\u0010\r\u001a\u00020\b2\b\b\u0002\u0010\u000e\u001a\u00020\b2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00102\b\b\u0002\u0010\u0012\u001a\u00020\u00102\b\b\u0002\u0010\u0013\u001a\u00020\u00102\b\b\u0002\u0010\u0014\u001a\u00020\u00102\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\n2\b\b\u0002\u0010\u0016\u001a\u00020\bH\u00c6\u0001J\u0013\u0010>\u001a\u00020\b2\b\u0010?\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010@\u001a\u00020AH\u00d6\u0001J\t\u0010B\u001a\u00020\nH\u00d6\u0001R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u000e\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0019R\u0011\u0010\u0016\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0019R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u0014\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0011\u0010\u0012\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001fR\u0011\u0010\u0013\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001fR\u0011\u0010\r\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u0019R\u0013\u0010\u0015\u001a\u0004\u0018\u00010\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010#R\u0011\u0010\f\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u0019R\u0011\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010!R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\u001dR\u0011\u0010\u0011\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\u001fR\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010\u001f\u00a8\u0006C"}, d2 = {"Lcom/example/drowsiness/logging/DecisionEntity;", "", "id", "", "timestampMs", "probability", "", "alertActive", "", "reason", "", "threshold", "steeringQualityPassed", "seatbeltQualityPassed", "continuousContact", "windowStartS", "", "windowEndS", "rmssdMs", "sdnnMs", "pnn50", "selectedChannel", "fusionDisagreement", "(JJFZLjava/lang/String;FZZZDDDDDLjava/lang/String;Z)V", "getAlertActive", "()Z", "getContinuousContact", "getFusionDisagreement", "getId", "()J", "getPnn50", "()D", "getProbability", "()F", "getReason", "()Ljava/lang/String;", "getRmssdMs", "getSdnnMs", "getSeatbeltQualityPassed", "getSelectedChannel", "getSteeringQualityPassed", "getThreshold", "getTimestampMs", "getWindowEndS", "getWindowStartS", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
@androidx.room.Entity(tableName = "decisions")
public final class DecisionEntity {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final long id = 0L;
    
    /**
     * System timestamp when the decision was made.
     */
    private final long timestampMs = 0L;
    
    /**
     * Model output probability (NaN if quality gate failed).
     */
    private final float probability = 0.0F;
    
    /**
     * Whether the drowsiness alert is currently active.
     */
    private final boolean alertActive = false;
    
    /**
     * Human-readable reason for this decision state.
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String reason = null;
    
    /**
     * Snapshot of the alert threshold from config at decision time.
     */
    private final float threshold = 0.0F;
    
    /**
     * Whether the steering-wheel channel passed contact quality.
     */
    private final boolean steeringQualityPassed = false;
    
    /**
     * Whether the seatbelt channel passed contact quality.
     */
    private final boolean seatbeltQualityPassed = false;
    
    /**
     * Whether contact was continuous (no handoff) across the window.
     */
    private final boolean continuousContact = false;
    
    /**
     * Window start timestamp in seconds (ECG timeline).
     */
    private final double windowStartS = 0.0;
    
    /**
     * Window end timestamp in seconds (ECG timeline).
     */
    private final double windowEndS = 0.0;
    
    /**
     * RMSSD HRV feature in milliseconds.
     */
    private final double rmssdMs = 0.0;
    
    /**
     * SDNN HRV feature in milliseconds.
     */
    private final double sdnnMs = 0.0;
    
    /**
     * pNN50 HRV feature (proportion).
     */
    private final double pnn50 = 0.0;
    
    /**
     * Which channel was selected by fusion (null if neither valid).
     */
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String selectedChannel = null;
    
    /**
     * Whether the two channels disagreed beyond threshold.
     */
    private final boolean fusionDisagreement = false;
    
    public DecisionEntity(long id, long timestampMs, float probability, boolean alertActive, @org.jetbrains.annotations.NotNull()
    java.lang.String reason, float threshold, boolean steeringQualityPassed, boolean seatbeltQualityPassed, boolean continuousContact, double windowStartS, double windowEndS, double rmssdMs, double sdnnMs, double pnn50, @org.jetbrains.annotations.Nullable()
    java.lang.String selectedChannel, boolean fusionDisagreement) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    /**
     * System timestamp when the decision was made.
     */
    public final long getTimestampMs() {
        return 0L;
    }
    
    /**
     * Model output probability (NaN if quality gate failed).
     */
    public final float getProbability() {
        return 0.0F;
    }
    
    /**
     * Whether the drowsiness alert is currently active.
     */
    public final boolean getAlertActive() {
        return false;
    }
    
    /**
     * Human-readable reason for this decision state.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getReason() {
        return null;
    }
    
    /**
     * Snapshot of the alert threshold from config at decision time.
     */
    public final float getThreshold() {
        return 0.0F;
    }
    
    /**
     * Whether the steering-wheel channel passed contact quality.
     */
    public final boolean getSteeringQualityPassed() {
        return false;
    }
    
    /**
     * Whether the seatbelt channel passed contact quality.
     */
    public final boolean getSeatbeltQualityPassed() {
        return false;
    }
    
    /**
     * Whether contact was continuous (no handoff) across the window.
     */
    public final boolean getContinuousContact() {
        return false;
    }
    
    /**
     * Window start timestamp in seconds (ECG timeline).
     */
    public final double getWindowStartS() {
        return 0.0;
    }
    
    /**
     * Window end timestamp in seconds (ECG timeline).
     */
    public final double getWindowEndS() {
        return 0.0;
    }
    
    /**
     * RMSSD HRV feature in milliseconds.
     */
    public final double getRmssdMs() {
        return 0.0;
    }
    
    /**
     * SDNN HRV feature in milliseconds.
     */
    public final double getSdnnMs() {
        return 0.0;
    }
    
    /**
     * pNN50 HRV feature (proportion).
     */
    public final double getPnn50() {
        return 0.0;
    }
    
    /**
     * Which channel was selected by fusion (null if neither valid).
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSelectedChannel() {
        return null;
    }
    
    /**
     * Whether the two channels disagreed beyond threshold.
     */
    public final boolean getFusionDisagreement() {
        return false;
    }
    
    public final long component1() {
        return 0L;
    }
    
    public final double component10() {
        return 0.0;
    }
    
    public final double component11() {
        return 0.0;
    }
    
    public final double component12() {
        return 0.0;
    }
    
    public final double component13() {
        return 0.0;
    }
    
    public final double component14() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component15() {
        return null;
    }
    
    public final boolean component16() {
        return false;
    }
    
    public final long component2() {
        return 0L;
    }
    
    public final float component3() {
        return 0.0F;
    }
    
    public final boolean component4() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    public final float component6() {
        return 0.0F;
    }
    
    public final boolean component7() {
        return false;
    }
    
    public final boolean component8() {
        return false;
    }
    
    public final boolean component9() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.logging.DecisionEntity copy(long id, long timestampMs, float probability, boolean alertActive, @org.jetbrains.annotations.NotNull()
    java.lang.String reason, float threshold, boolean steeringQualityPassed, boolean seatbeltQualityPassed, boolean continuousContact, double windowStartS, double windowEndS, double rmssdMs, double sdnnMs, double pnn50, @org.jetbrains.annotations.Nullable()
    java.lang.String selectedChannel, boolean fusionDisagreement) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}