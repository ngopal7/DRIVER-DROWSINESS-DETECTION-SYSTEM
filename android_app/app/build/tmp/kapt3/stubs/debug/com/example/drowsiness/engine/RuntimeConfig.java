package com.example.drowsiness.engine;

/**
 * Runtime-configurable on-device alert policy.
 *
 * Moved from root package to `engine/` sub-package. Model is supplied by a
 * TFLite/ONNX adapter; features are supplied by [OnDeviceFeaturePipeline].
 *
 * Config values come from `pipeline_config.yaml`'s `android` section —
 * never bake study thresholds into code.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\tH\u00c6\u0003J\t\u0010\u001a\u001a\u00020\tH\u00c6\u0003JE\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\tH\u00c6\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001f\u001a\u00020\tH\u00d6\u0001J\t\u0010 \u001a\u00020!H\u00d6\u0001R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\n\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013\u00a8\u0006\""}, d2 = {"Lcom/example/drowsiness/engine/RuntimeConfig;", "", "featureUpdateSeconds", "", "inferenceSeconds", "alertThreshold", "", "clearThreshold", "alertDebounceCount", "", "clearDebounceCount", "(JJFFII)V", "getAlertDebounceCount", "()I", "getAlertThreshold", "()F", "getClearDebounceCount", "getClearThreshold", "getFeatureUpdateSeconds", "()J", "getInferenceSeconds", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
public final class RuntimeConfig {
    private final long featureUpdateSeconds = 0L;
    private final long inferenceSeconds = 0L;
    private final float alertThreshold = 0.0F;
    private final float clearThreshold = 0.0F;
    private final int alertDebounceCount = 0;
    private final int clearDebounceCount = 0;
    
    public RuntimeConfig(long featureUpdateSeconds, long inferenceSeconds, float alertThreshold, float clearThreshold, int alertDebounceCount, int clearDebounceCount) {
        super();
    }
    
    public final long getFeatureUpdateSeconds() {
        return 0L;
    }
    
    public final long getInferenceSeconds() {
        return 0L;
    }
    
    public final float getAlertThreshold() {
        return 0.0F;
    }
    
    public final float getClearThreshold() {
        return 0.0F;
    }
    
    public final int getAlertDebounceCount() {
        return 0;
    }
    
    public final int getClearDebounceCount() {
        return 0;
    }
    
    public final long component1() {
        return 0L;
    }
    
    public final long component2() {
        return 0L;
    }
    
    public final float component3() {
        return 0.0F;
    }
    
    public final float component4() {
        return 0.0F;
    }
    
    public final int component5() {
        return 0;
    }
    
    public final int component6() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.engine.RuntimeConfig copy(long featureUpdateSeconds, long inferenceSeconds, float alertThreshold, float clearThreshold, int alertDebounceCount, int clearDebounceCount) {
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