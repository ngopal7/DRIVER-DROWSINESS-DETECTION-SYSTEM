package com.example.drowsiness.config;

/**
 * Pipeline config loader mirroring `config.py`'s `load_config()`.
 *
 * Loads `pipeline_config.yaml` from Android assets and validates the same
 * required keys and constraints as the Python loader.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010$\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J$\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00072\u0006\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u0005J\u001a\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00072\u0006\u0010\f\u001a\u00020\u0005J$\u0010\r\u001a\u00020\u000e2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00072\u0006\u0010\u0010\u001a\u00020\u0005H\u0002J$\u0010\u0011\u001a\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00072\u0006\u0010\u0010\u001a\u00020\u0005H\u0002J$\u0010\u0013\u001a\u00020\u00142\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00072\u0006\u0010\u0010\u001a\u00020\u0005H\u0002J\u001a\u0010\u0015\u001a\u00020\u00162\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u0007R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/example/drowsiness/config/PipelineConfig;", "", "()V", "REQUIRED_KEYS", "", "", "load", "", "context", "Landroid/content/Context;", "assetPath", "parseAndValidate", "yamlContent", "requireFloat", "", "map", "key", "requireInt", "", "requireLong", "", "toRuntimeConfig", "Lcom/example/drowsiness/config/AndroidRuntimeConfig;", "config", "app_debug"})
public final class PipelineConfig {
    
    /**
     * Required top-level keys — matches Python's `REQUIRED` set exactly.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> REQUIRED_KEYS = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.drowsiness.config.PipelineConfig INSTANCE = null;
    
    private PipelineConfig() {
        super();
    }
    
    /**
     * Load config from Android assets.
     *
     * @param context   Android context for asset access.
     * @param assetPath Path within `assets/`, default `"pipeline_config.yaml"`.
     * @return Parsed config as a nested Map hierarchy.
     * @throws IllegalArgumentException on missing keys or invalid values.
     */
    @kotlin.Suppress(names = {"UNCHECKED_CAST"})
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Object> load(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String assetPath) {
        return null;
    }
    
    /**
     * Parse and validate a YAML string. Useful for testing without Android context.
     */
    @kotlin.Suppress(names = {"UNCHECKED_CAST"})
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Object> parseAndValidate(@org.jetbrains.annotations.NotNull()
    java.lang.String yamlContent) {
        return null;
    }
    
    /**
     * Extract the `android` section and convert to [RuntimeConfig].
     * Matches the data class in `DrowsinessEngine.kt`.
     */
    @kotlin.Suppress(names = {"UNCHECKED_CAST"})
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.config.AndroidRuntimeConfig toRuntimeConfig(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.lang.Object> config) {
        return null;
    }
    
    private final float requireFloat(java.util.Map<java.lang.String, ? extends java.lang.Object> map, java.lang.String key) {
        return 0.0F;
    }
    
    private final int requireInt(java.util.Map<java.lang.String, ? extends java.lang.Object> map, java.lang.String key) {
        return 0;
    }
    
    private final long requireLong(java.util.Map<java.lang.String, ? extends java.lang.Object> map, java.lang.String key) {
        return 0L;
    }
}