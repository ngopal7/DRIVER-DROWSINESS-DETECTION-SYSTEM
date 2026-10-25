package com.example.drowsiness.config

import android.content.Context
import org.snakeyaml.engine.v2.api.Load
import org.snakeyaml.engine.v2.api.LoadSettings

/**
 * Pipeline config loader mirroring `config.py`'s `load_config()`.
 *
 * Loads `pipeline_config.yaml` from Android assets and validates the same
 * required keys and constraints as the Python loader.
 */
object PipelineConfig {

    /** Required top-level keys — matches Python's `REQUIRED` set exactly. */
    private val REQUIRED_KEYS = setOf(
        "sampling_rate_hz", "hrv", "quality", "filter",
        "r_peaks", "fusion", "labeling", "modeling",
        "android", "sensor_geometry"
    )

    /**
     * Load config from Android assets.
     *
     * @param context   Android context for asset access.
     * @param assetPath Path within `assets/`, default `"pipeline_config.yaml"`.
     * @return Parsed config as a nested Map hierarchy.
     * @throws IllegalArgumentException on missing keys or invalid values.
     */
    @Suppress("UNCHECKED_CAST")
    fun load(
        context: Context,
        assetPath: String = "pipeline_config.yaml",
    ): Map<String, Any> {
        val yaml = context.assets.open(assetPath).bufferedReader().use { it.readText() }
        return parseAndValidate(yaml)
    }

    /**
     * Parse and validate a YAML string. Useful for testing without Android context.
     */
    @Suppress("UNCHECKED_CAST")
    fun parseAndValidate(yamlContent: String): Map<String, Any> {
        val settings = LoadSettings.builder().build()
        val loader = Load(settings)
        val raw = loader.loadFromString(yamlContent)
            ?: throw IllegalArgumentException("pipeline config is empty")

        val config = raw as? Map<String, Any>
            ?: throw IllegalArgumentException("pipeline config must be a YAML mapping")

        // Validate required keys
        val missing = REQUIRED_KEYS - config.keys
        if (missing.isNotEmpty()) {
            throw IllegalArgumentException("pipeline config missing: ${missing.sorted()}")
        }

        // Validate HRV overlap constraint
        val hrv = config["hrv"] as? Map<String, Any>
            ?: throw IllegalArgumentException("hrv section must be a mapping")
        val windowSeconds = (hrv["window_seconds"] as? Number)?.toDouble()
            ?: throw IllegalArgumentException("hrv.window_seconds is required")
        val overlapSeconds = (hrv["overlap_seconds"] as? Number)?.toDouble()
            ?: throw IllegalArgumentException("hrv.overlap_seconds is required")
        if (overlapSeconds >= windowSeconds) {
            throw IllegalArgumentException("overlap_seconds must be below window_seconds")
        }

        return config
    }

    /**
     * Extract the `android` section and convert to [RuntimeConfig].
     * Matches the data class in `DrowsinessEngine.kt`.
     */
    @Suppress("UNCHECKED_CAST")
    fun toRuntimeConfig(config: Map<String, Any>): AndroidRuntimeConfig {
        val android = config["android"] as? Map<String, Any>
            ?: throw IllegalArgumentException("android section missing from config")
        return AndroidRuntimeConfig(
            featureUpdateSeconds = requireLong(android, "feature_update_seconds"),
            inferenceSeconds = requireLong(android, "inference_seconds"),
            alertThreshold = requireFloat(android, "alert_threshold"),
            clearThreshold = requireFloat(android, "clear_threshold"),
            targetFalsePositiveTolerance = requireFloat(android, "target_false_positive_tolerance"),
            alertDebounceCount = requireInt(android, "alert_debounce_count"),
            clearDebounceCount = requireInt(android, "clear_debounce_count"),
        )
    }

    private fun requireFloat(map: Map<String, Any>, key: String): Float =
        (map[key] as? Number)?.toFloat()
            ?: throw IllegalArgumentException("android.$key is required")

    private fun requireInt(map: Map<String, Any>, key: String): Int =
        (map[key] as? Number)?.toInt()
            ?: throw IllegalArgumentException("android.$key is required")

    private fun requireLong(map: Map<String, Any>, key: String): Long =
        (map[key] as? Number)?.toLong()
            ?: throw IllegalArgumentException("android.$key is required")
}

/**
 * Typed representation of the `android` config section.
 * Can be converted to [RuntimeConfig] for [DrowsinessEngine].
 */
data class AndroidRuntimeConfig(
    val featureUpdateSeconds: Long,
    val inferenceSeconds: Long,
    val alertThreshold: Float,
    val clearThreshold: Float,
    val targetFalsePositiveTolerance: Float,
    val alertDebounceCount: Int,
    val clearDebounceCount: Int,
)
