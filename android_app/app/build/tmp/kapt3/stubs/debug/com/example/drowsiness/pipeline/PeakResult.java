package com.example.drowsiness.pipeline;

/**
 * R-peak detection output for a single channel.
 * Maps to Python `PeakResult`.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0015\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0013\n\u0000\n\u0002\u0010$\n\u0002\b\u0014\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BA\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u0012\u0014\b\u0002\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0007H\u00c6\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u0015\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\t0\u000bH\u00c6\u0003JI\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\u0014\b\u0002\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\t0\u000bH\u00c6\u0001J\u0013\u0010\u001d\u001a\u00020\u00072\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010\u001f\u001a\u00020 H\u0016J\t\u0010!\u001a\u00020\u0005H\u00d6\u0001R\u0013\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001d\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006\""}, d2 = {"Lcom/example/drowsiness/pipeline/PeakResult;", "", "indices", "", "method", "", "fallbackUsed", "", "confidence", "", "confidenceComponents", "", "([ILjava/lang/String;Z[DLjava/util/Map;)V", "getConfidence", "()[D", "getConfidenceComponents", "()Ljava/util/Map;", "getFallbackUsed", "()Z", "getIndices", "()[I", "getMethod", "()Ljava/lang/String;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class PeakResult {
    @org.jetbrains.annotations.NotNull()
    private final int[] indices = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String method = null;
    private final boolean fallbackUsed = false;
    @org.jetbrains.annotations.Nullable()
    private final double[] confidence = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, double[]> confidenceComponents = null;
    
    public PeakResult(@org.jetbrains.annotations.NotNull()
    int[] indices, @org.jetbrains.annotations.NotNull()
    java.lang.String method, boolean fallbackUsed, @org.jetbrains.annotations.Nullable()
    double[] confidence, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, double[]> confidenceComponents) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final int[] getIndices() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMethod() {
        return null;
    }
    
    public final boolean getFallbackUsed() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final double[] getConfidence() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, double[]> getConfidenceComponents() {
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
    public final java.lang.String component2() {
        return null;
    }
    
    public final boolean component3() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final double[] component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, double[]> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.pipeline.PeakResult copy(@org.jetbrains.annotations.NotNull()
    int[] indices, @org.jetbrains.annotations.NotNull()
    java.lang.String method, boolean fallbackUsed, @org.jetbrains.annotations.Nullable()
    double[] confidence, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, double[]> confidenceComponents) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}