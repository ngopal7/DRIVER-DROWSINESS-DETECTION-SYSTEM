package com.example.drowsiness.pipeline;

/**
 * Final fusion result for a window.
 * Maps to Python `FusionResult`.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0015\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010$\n\u0002\b\u0016\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BG\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0016\b\u0002\u0010\u000b\u001a\u0010\u0012\u0004\u0012\u00020\n\u0012\u0006\u0012\u0004\u0018\u00010\u00010\f\u00a2\u0006\u0002\u0010\rJ\u000b\u0010\u0019\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\nH\u00c6\u0003J\u0017\u0010\u001e\u001a\u0010\u0012\u0004\u0012\u00020\n\u0012\u0006\u0012\u0004\u0018\u00010\u00010\fH\u00c6\u0003JU\u0010\u001f\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\n2\u0016\b\u0002\u0010\u000b\u001a\u0010\u0012\u0004\u0012\u00020\n\u0012\u0006\u0012\u0004\u0018\u00010\u00010\fH\u00c6\u0001J\u0013\u0010 \u001a\u00020\u00072\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010\"\u001a\u00020#H\u0016J\t\u0010$\u001a\u00020\nH\u00d6\u0001R\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u001f\u0010\u000b\u001a\u0010\u0012\u0004\u0012\u00020\n\u0012\u0006\u0012\u0004\u0018\u00010\u00010\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u000f\u00a8\u0006%"}, d2 = {"Lcom/example/drowsiness/pipeline/FusionResult;", "", "selectedChannel", "Lcom/example/drowsiness/pipeline/EcgChannel;", "peakIndices", "", "valid", "", "disagreement", "reason", "", "metadata", "", "(Lcom/example/drowsiness/pipeline/EcgChannel;[IZZLjava/lang/String;Ljava/util/Map;)V", "getDisagreement", "()Z", "getMetadata", "()Ljava/util/Map;", "getPeakIndices", "()[I", "getReason", "()Ljava/lang/String;", "getSelectedChannel", "()Lcom/example/drowsiness/pipeline/EcgChannel;", "getValid", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class FusionResult {
    @org.jetbrains.annotations.Nullable()
    private final com.example.drowsiness.pipeline.EcgChannel selectedChannel = null;
    @org.jetbrains.annotations.NotNull()
    private final int[] peakIndices = null;
    private final boolean valid = false;
    private final boolean disagreement = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String reason = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.Object> metadata = null;
    
    public FusionResult(@org.jetbrains.annotations.Nullable()
    com.example.drowsiness.pipeline.EcgChannel selectedChannel, @org.jetbrains.annotations.NotNull()
    int[] peakIndices, boolean valid, boolean disagreement, @org.jetbrains.annotations.NotNull()
    java.lang.String reason, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.lang.Object> metadata) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.drowsiness.pipeline.EcgChannel getSelectedChannel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final int[] getPeakIndices() {
        return null;
    }
    
    public final boolean getValid() {
        return false;
    }
    
    public final boolean getDisagreement() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getReason() {
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
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.drowsiness.pipeline.EcgChannel component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final int[] component2() {
        return null;
    }
    
    public final boolean component3() {
        return false;
    }
    
    public final boolean component4() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Object> component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.pipeline.FusionResult copy(@org.jetbrains.annotations.Nullable()
    com.example.drowsiness.pipeline.EcgChannel selectedChannel, @org.jetbrains.annotations.NotNull()
    int[] peakIndices, boolean valid, boolean disagreement, @org.jetbrains.annotations.NotNull()
    java.lang.String reason, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.lang.Object> metadata) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}