package com.example.drowsiness.pipeline;

/**
 * One SQI-gated ECG channel's detected beats for fusion.
 * Maps to Python `ChannelSeries`.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u0013\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B1\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\tH\u00c6\u0003J\u000b\u0010\u001b\u001a\u0004\u0018\u00010\u000bH\u00c6\u0003J=\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000bH\u00c6\u0001J\u0013\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010 \u001a\u00020!H\u00d6\u0001J\t\u0010\"\u001a\u00020#H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0013\u0010\n\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006$"}, d2 = {"Lcom/example/drowsiness/pipeline/ChannelSeries;", "", "channel", "Lcom/example/drowsiness/pipeline/EcgChannel;", "peaks", "Lcom/example/drowsiness/pipeline/PeakResult;", "quality", "Lcom/example/drowsiness/pipeline/QualityResult;", "fs", "", "motionArtifactEstimate", "", "(Lcom/example/drowsiness/pipeline/EcgChannel;Lcom/example/drowsiness/pipeline/PeakResult;Lcom/example/drowsiness/pipeline/QualityResult;D[D)V", "getChannel", "()Lcom/example/drowsiness/pipeline/EcgChannel;", "getFs", "()D", "getMotionArtifactEstimate", "()[D", "getPeaks", "()Lcom/example/drowsiness/pipeline/PeakResult;", "getQuality", "()Lcom/example/drowsiness/pipeline/QualityResult;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
public final class ChannelSeries {
    @org.jetbrains.annotations.NotNull()
    private final com.example.drowsiness.pipeline.EcgChannel channel = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.drowsiness.pipeline.PeakResult peaks = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.drowsiness.pipeline.QualityResult quality = null;
    private final double fs = 0.0;
    @org.jetbrains.annotations.Nullable()
    private final double[] motionArtifactEstimate = null;
    
    public ChannelSeries(@org.jetbrains.annotations.NotNull()
    com.example.drowsiness.pipeline.EcgChannel channel, @org.jetbrains.annotations.NotNull()
    com.example.drowsiness.pipeline.PeakResult peaks, @org.jetbrains.annotations.NotNull()
    com.example.drowsiness.pipeline.QualityResult quality, double fs, @org.jetbrains.annotations.Nullable()
    double[] motionArtifactEstimate) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.pipeline.EcgChannel getChannel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.pipeline.PeakResult getPeaks() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.pipeline.QualityResult getQuality() {
        return null;
    }
    
    public final double getFs() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final double[] getMotionArtifactEstimate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.pipeline.EcgChannel component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.pipeline.PeakResult component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.pipeline.QualityResult component3() {
        return null;
    }
    
    public final double component4() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final double[] component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.pipeline.ChannelSeries copy(@org.jetbrains.annotations.NotNull()
    com.example.drowsiness.pipeline.EcgChannel channel, @org.jetbrains.annotations.NotNull()
    com.example.drowsiness.pipeline.PeakResult peaks, @org.jetbrains.annotations.NotNull()
    com.example.drowsiness.pipeline.QualityResult quality, double fs, @org.jetbrains.annotations.Nullable()
    double[] motionArtifactEstimate) {
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