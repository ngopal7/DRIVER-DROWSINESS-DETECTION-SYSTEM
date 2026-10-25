package com.example.drowsiness.pipeline;

/**
 * Strategy-based channel fusion mirroring `signal_processing/channel_fusion.py`.
 *
 * Three strategies: `priority_only`, `compare_and_flag`, `full_fusion`.
 * Never silently treats channel agreement as gold-standard truth.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0015\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0010\u0013\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002JC\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\u000b2\u0016\b\u0002\u0010\f\u001a\u0010\u0012\u0004\u0012\u00020\t\u0012\u0006\u0012\u0004\u0018\u00010\u00010\rH\u0002\u00a2\u0006\u0002\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002JL\u0010\u0013\u001a\"\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u000b\u0012\u0012\u0012\u0010\u0012\u0004\u0012\u00020\t\u0012\u0006\u0012\u0004\u0018\u00010\u00010\r0\u00142\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00172\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\rH\u0002J,\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00172\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\rH\u0002J4\u0010\u001b\u001a\u00020\u00042\u0006\u0010\u001c\u001a\u00020\u00172\u0006\u0010\u001d\u001a\u00020\u00172\b\u0010\u001e\u001a\u0004\u0018\u00010\t2\u0012\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\rJ~\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u00102\u0006\u0010#\u001a\u00020\u00102\u0006\u0010$\u001a\u00020\u00102\u0006\u0010%\u001a\u00020&2\u0006\u0010\'\u001a\u00020&2\u0006\u0010(\u001a\u00020\u00062\u0006\u0010)\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u000b2\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\r2\n\b\u0002\u0010*\u001a\u0004\u0018\u00010\u00122\n\b\u0002\u0010+\u001a\u0004\u0018\u00010\u00122\n\b\u0002\u0010,\u001a\u0004\u0018\u00010\u0010J\u0010\u0010-\u001a\u00020\u00152\u0006\u0010.\u001a\u00020\u0017H\u0002J\u0018\u0010/\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\u00102\u0006\u0010\u0018\u001a\u00020\u0010H\u0002J\u0018\u00100\u001a\u00020\u000b2\u0006\u00101\u001a\u00020\u00102\u0006\u00102\u001a\u00020\u000bH\u0002J\u0018\u00103\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0017H\u0002J\u0014\u00104\u001a\u000205*\u00020\u00062\u0006\u00106\u001a\u000205H\u0002\u00a8\u00067"}, d2 = {"Lcom/example/drowsiness/pipeline/ChannelFusion;", "", "()V", "asRR", "Lcom/example/drowsiness/pipeline/RRSeries;", "peaks", "", "sources", "", "", "fs", "", "extraMeta", "", "([I[Ljava/lang/String;DLjava/util/Map;)Lcom/example/drowsiness/pipeline/RRSeries;", "confidence", "", "result", "Lcom/example/drowsiness/pipeline/PeakResult;", "disagreement", "Lkotlin/Triple;", "", "a", "Lcom/example/drowsiness/pipeline/ChannelSeries;", "b", "cfg", "fullFusion", "fuse", "channelA", "channelB", "strategy", "config", "fuseChannels", "Lcom/example/drowsiness/pipeline/FusionResult;", "steeringSig", "seatbeltSig", "ground", "steeringQuality", "Lcom/example/drowsiness/pipeline/QualityResult;", "seatbeltQuality", "steeringPeaks", "seatbeltPeaks", "steeringResult", "seatbeltResult", "motionArtifactEstimate", "isValid", "series", "pearsonCorrelation", "percentile", "data", "p", "priority", "binarySearchInsertionPoint", "", "value", "app_debug"})
public final class ChannelFusion {
    @org.jetbrains.annotations.NotNull()
    public static final com.example.drowsiness.pipeline.ChannelFusion INSTANCE = null;
    
    private ChannelFusion() {
        super();
    }
    
    /**
     * Select a named fusion strategy. Mirrors `fuse()` in Python.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.pipeline.RRSeries fuse(@org.jetbrains.annotations.NotNull()
    com.example.drowsiness.pipeline.ChannelSeries channelA, @org.jetbrains.annotations.NotNull()
    com.example.drowsiness.pipeline.ChannelSeries channelB, @org.jetbrains.annotations.Nullable()
    java.lang.String strategy, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.lang.Object> config) {
        return null;
    }
    
    /**
     * Compatibility adapter matching `fuse_channels()` in Python.
     * Ground cannot enter the ChannelSeries interface.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.pipeline.FusionResult fuseChannels(@org.jetbrains.annotations.NotNull()
    double[] steeringSig, @org.jetbrains.annotations.NotNull()
    double[] seatbeltSig, @org.jetbrains.annotations.NotNull()
    double[] ground, @org.jetbrains.annotations.NotNull()
    com.example.drowsiness.pipeline.QualityResult steeringQuality, @org.jetbrains.annotations.NotNull()
    com.example.drowsiness.pipeline.QualityResult seatbeltQuality, @org.jetbrains.annotations.NotNull()
    int[] steeringPeaks, @org.jetbrains.annotations.NotNull()
    int[] seatbeltPeaks, double fs, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.lang.Object> cfg, @org.jetbrains.annotations.Nullable()
    com.example.drowsiness.pipeline.PeakResult steeringResult, @org.jetbrains.annotations.Nullable()
    com.example.drowsiness.pipeline.PeakResult seatbeltResult, @org.jetbrains.annotations.Nullable()
    double[] motionArtifactEstimate) {
        return null;
    }
    
    private final boolean isValid(com.example.drowsiness.pipeline.ChannelSeries series) {
        return false;
    }
    
    private final double[] confidence(com.example.drowsiness.pipeline.PeakResult result) {
        return null;
    }
    
    private final com.example.drowsiness.pipeline.RRSeries asRR(int[] peaks, java.lang.String[] sources, double fs, java.util.Map<java.lang.String, ? extends java.lang.Object> extraMeta) {
        return null;
    }
    
    private final com.example.drowsiness.pipeline.RRSeries priority(com.example.drowsiness.pipeline.ChannelSeries a, com.example.drowsiness.pipeline.ChannelSeries b) {
        return null;
    }
    
    private final kotlin.Triple<java.lang.Boolean, java.lang.Double, java.util.Map<java.lang.String, java.lang.Object>> disagreement(com.example.drowsiness.pipeline.ChannelSeries a, com.example.drowsiness.pipeline.ChannelSeries b, java.util.Map<java.lang.String, ? extends java.lang.Object> cfg) {
        return null;
    }
    
    private final com.example.drowsiness.pipeline.RRSeries fullFusion(com.example.drowsiness.pipeline.ChannelSeries a, com.example.drowsiness.pipeline.ChannelSeries b, java.util.Map<java.lang.String, ? extends java.lang.Object> cfg) {
        return null;
    }
    
    private final int binarySearchInsertionPoint(int[] $this$binarySearchInsertionPoint, int value) {
        return 0;
    }
    
    private final double pearsonCorrelation(double[] a, double[] b) {
        return 0.0;
    }
    
    private final double percentile(double[] data, double p) {
        return 0.0;
    }
}