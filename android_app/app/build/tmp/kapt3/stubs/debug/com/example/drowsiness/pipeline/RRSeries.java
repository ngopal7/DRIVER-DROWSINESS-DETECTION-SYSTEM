package com.example.drowsiness.pipeline;

/**
 * RR intervals that never span a channel handoff or contact discontinuity.
 * Maps to Python `RRSeries`.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0015\n\u0000\n\u0002\u0010\u0013\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\b\u001d\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001Ba\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0006\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\n\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u0003\u0012\u0016\b\u0002\u0010\u000f\u001a\u0010\u0012\u0004\u0012\u00020\b\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u0010\u00a2\u0006\u0002\u0010\u0011J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0005H\u00c6\u0003J\u0014\u0010#\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0013J\t\u0010$\u001a\u00020\nH\u00c6\u0003J\t\u0010%\u001a\u00020\nH\u00c6\u0003J\t\u0010&\u001a\u00020\rH\u00c6\u0003J\t\u0010\'\u001a\u00020\u0003H\u00c6\u0003J\u0017\u0010(\u001a\u0010\u0012\u0004\u0012\u00020\b\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u0010H\u00c6\u0003Jr\u0010)\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u00032\u0016\b\u0002\u0010\u000f\u001a\u0010\u0012\u0004\u0012\u00020\b\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u0010H\u00c6\u0001\u00a2\u0006\u0002\u0010*J\u0013\u0010+\u001a\u00020\n2\b\u0010,\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010-\u001a\u00020.H\u0016J\t\u0010/\u001a\u00020\bH\u00d6\u0001R\u0019\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\n\n\u0002\u0010\u0014\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u000b\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u000e\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u001f\u0010\u000f\u001a\u0010\u0012\u0004\u0012\u00020\b\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001aR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0016\u00a8\u00060"}, d2 = {"Lcom/example/drowsiness/pipeline/RRSeries;", "", "peakIndices", "", "rrMs", "", "beatSources", "", "", "valid", "", "disagreement", "disagreementMs", "", "handoffBoundaries", "metadata", "", "([I[D[Ljava/lang/String;ZZD[ILjava/util/Map;)V", "getBeatSources", "()[Ljava/lang/String;", "[Ljava/lang/String;", "getDisagreement", "()Z", "getDisagreementMs", "()D", "getHandoffBoundaries", "()[I", "getMetadata", "()Ljava/util/Map;", "getPeakIndices", "getRrMs", "()[D", "getValid", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "([I[D[Ljava/lang/String;ZZD[ILjava/util/Map;)Lcom/example/drowsiness/pipeline/RRSeries;", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class RRSeries {
    @org.jetbrains.annotations.NotNull()
    private final int[] peakIndices = null;
    @org.jetbrains.annotations.NotNull()
    private final double[] rrMs = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String[] beatSources = null;
    private final boolean valid = false;
    private final boolean disagreement = false;
    private final double disagreementMs = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final int[] handoffBoundaries = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.Object> metadata = null;
    
    public RRSeries(@org.jetbrains.annotations.NotNull()
    int[] peakIndices, @org.jetbrains.annotations.NotNull()
    double[] rrMs, @org.jetbrains.annotations.NotNull()
    java.lang.String[] beatSources, boolean valid, boolean disagreement, double disagreementMs, @org.jetbrains.annotations.NotNull()
    int[] handoffBoundaries, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Object> metadata) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final int[] getPeakIndices() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final double[] getRrMs() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String[] getBeatSources() {
        return null;
    }
    
    public final boolean getValid() {
        return false;
    }
    
    public final boolean getDisagreement() {
        return false;
    }
    
    public final double getDisagreementMs() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final int[] getHandoffBoundaries() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Object> getMetadata() {
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
    
    @org.jetbrains.annotations.NotNull()
    public final int[] component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final double[] component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String[] component3() {
        return null;
    }
    
    public final boolean component4() {
        return false;
    }
    
    public final boolean component5() {
        return false;
    }
    
    public final double component6() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final int[] component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Object> component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.pipeline.RRSeries copy(@org.jetbrains.annotations.NotNull()
    int[] peakIndices, @org.jetbrains.annotations.NotNull()
    double[] rrMs, @org.jetbrains.annotations.NotNull()
    java.lang.String[] beatSources, boolean valid, boolean disagreement, double disagreementMs, @org.jetbrains.annotations.NotNull()
    int[] handoffBoundaries, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Object> metadata) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}