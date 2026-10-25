package com.example.drowsiness.pipeline;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000>\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010$\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0010\u0013\n\u0002\b\u0002\n\u0002\u0010\u0018\n\u0002\b\u0002\u001a*\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n\u0012\u0002\b\u0003\u0012\u0002\b\u00030\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0001H\u0000\u001a1\u0010\u0007\u001a\u00020\b2\u000e\u0010\u0002\u001a\n\u0012\u0002\b\u0003\u0012\u0002\b\u00030\u00032\u0006\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\bH\u0000\u00a2\u0006\u0002\u0010\t\u001a1\u0010\n\u001a\u00020\u000b2\u000e\u0010\u0002\u001a\n\u0012\u0002\b\u0003\u0012\u0002\b\u00030\u00032\u0006\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u000bH\u0000\u00a2\u0006\u0002\u0010\f\u001a0\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000e0\u00032\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000e0\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0000\u001a4\u0010\u0010\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000e\u0018\u00010\u00032\u0014\u0010\u000f\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000e\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0000\u001a,\u0010\u0011\u001a\u00020\u00052\u000e\u0010\u0002\u001a\n\u0012\u0002\b\u0003\u0012\u0002\b\u00030\u00032\u0006\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005H\u0000\u001a(\u0010\u0012\u001a\u0004\u0018\u00010\u00052\u0014\u0010\u000f\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000e\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0000\u001a\u0010\u0010\u0013\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\u0015H\u0000\u001a\u0010\u0010\u0016\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\u0015H\u0000\u001a\u0018\u0010\u0017\u001a\u00020\u0001*\u0004\u0018\u00010\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0018H\u0000\u001a\u0018\u0010\u0017\u001a\u00020\u0001*\u0004\u0018\u00010\u00152\b\u0010\u0019\u001a\u0004\u0018\u00010\u0015H\u0000\u00a8\u0006\u001a"}, d2 = {"configBool", "", "section", "", "key", "", "default", "configDouble", "", "(Ljava/util/Map;Ljava/lang/String;Ljava/lang/Double;)D", "configInt", "", "(Ljava/util/Map;Ljava/lang/String;Ljava/lang/Integer;)I", "configSection", "", "config", "configSectionOrNull", "configString", "configStringOrNull", "median", "data", "", "std", "contentEquals", "", "other", "app_debug"})
public final class ConfigUtilsKt {
    
    /**
     * Config access helpers shared across pipeline stages.
     *
     * All pipeline modules use these to look up values from the parsed YAML
     * config map using the same key paths as the Python code.
     */
    @kotlin.Suppress(names = {"UNCHECKED_CAST"})
    @org.jetbrains.annotations.NotNull()
    public static final java.util.Map<java.lang.String, java.lang.Object> configSection(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.lang.Object> config, @org.jetbrains.annotations.NotNull()
    java.lang.String key) {
        return null;
    }
    
    @kotlin.Suppress(names = {"UNCHECKED_CAST"})
    @org.jetbrains.annotations.Nullable()
    public static final java.util.Map<java.lang.String, java.lang.Object> configSectionOrNull(@org.jetbrains.annotations.Nullable()
    java.util.Map<java.lang.String, ? extends java.lang.Object> config, @org.jetbrains.annotations.NotNull()
    java.lang.String key) {
        return null;
    }
    
    public static final double configDouble(@org.jetbrains.annotations.NotNull()
    java.util.Map<?, ?> section, @org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.Nullable()
    java.lang.Double p2_772401952) {
        return 0.0;
    }
    
    public static final int configInt(@org.jetbrains.annotations.NotNull()
    java.util.Map<?, ?> section, @org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.Nullable()
    java.lang.Integer p2_772401952) {
        return 0;
    }
    
    public static final boolean configBool(@org.jetbrains.annotations.NotNull()
    java.util.Map<?, ?> section, @org.jetbrains.annotations.NotNull()
    java.lang.String key, boolean p2_772401952) {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String configString(@org.jetbrains.annotations.NotNull()
    java.util.Map<?, ?> section, @org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.Nullable()
    java.lang.String p2_772401952) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public static final java.lang.String configStringOrNull(@org.jetbrains.annotations.Nullable()
    java.util.Map<java.lang.String, ? extends java.lang.Object> config, @org.jetbrains.annotations.NotNull()
    java.lang.String key) {
        return null;
    }
    
    /**
     * Median of a DoubleArray.  Returns NaN for empty arrays.
     */
    public static final double median(@org.jetbrains.annotations.NotNull()
    double[] data) {
        return 0.0;
    }
    
    /**
     * Standard deviation (population, ddof=0).
     */
    public static final double std(@org.jetbrains.annotations.NotNull()
    double[] data) {
        return 0.0;
    }
    
    /**
     * Content-equality for nullable DoubleArray.
     */
    public static final boolean contentEquals(@org.jetbrains.annotations.Nullable()
    double[] $this$contentEquals, @org.jetbrains.annotations.Nullable()
    double[] other) {
        return false;
    }
    
    /**
     * Content-equality for nullable BooleanArray.
     */
    public static final boolean contentEquals(@org.jetbrains.annotations.Nullable()
    boolean[] $this$contentEquals, @org.jetbrains.annotations.Nullable()
    boolean[] other) {
        return false;
    }
}