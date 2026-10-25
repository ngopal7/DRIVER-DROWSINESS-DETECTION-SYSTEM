package com.example.drowsiness.pipeline;

/**
 * HRV feature computation mirroring `signal_processing/hrv_features.py`.
 *
 * Only invoke after a passed continuous-contact gate.
 * Features: rmssd_ms, sdnn_ms, pnn50, n_rr (and reserved lf_hf).
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0003\n\u0002\u0010\u0014\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J6\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\n0\t2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\n2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\tJ\u001a\u0010\u000f\u001a\u00020\u00102\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\n0\tR\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/example/drowsiness/pipeline/HrvFeatures;", "", "()V", "FEATURE_NAMES", "", "", "getFEATURE_NAMES", "()Ljava/util/List;", "computeHrvFeatures", "", "", "peaks", "", "fs", "cfg", "featureVector", "", "features", "app_debug"})
public final class HrvFeatures {
    
    /**
     * Feature names in canonical order, matching Python DEFAULT_FEATURES.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> FEATURE_NAMES = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.drowsiness.pipeline.HrvFeatures INSTANCE = null;
    
    private HrvFeatures() {
        super();
    }
    
    /**
     * Feature names in canonical order, matching Python DEFAULT_FEATURES.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getFEATURE_NAMES() {
        return null;
    }
    
    /**
     * Compute HRV features from R-peak indices.
     * Mirrors `compute_hrv_features()` in Python.
     *
     * @param peaks  Sorted R-peak sample indices.
     * @param fs     Sampling rate in Hz.
     * @param cfg    Pipeline config (uses `hrv.include_lf_hf`).
     * @return Map with keys: rmssd_ms, sdnn_ms, pnn50, n_rr (and lf_hf if enabled).
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Double> computeHrvFeatures(@org.jetbrains.annotations.NotNull()
    int[] peaks, double fs, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.lang.Object> cfg) {
        return null;
    }
    
    /**
     * Convert feature map to ordered FloatArray for model inference.
     * Order matches [FEATURE_NAMES].
     */
    @org.jetbrains.annotations.NotNull()
    public final float[] featureVector(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Double> features) {
        return null;
    }
}