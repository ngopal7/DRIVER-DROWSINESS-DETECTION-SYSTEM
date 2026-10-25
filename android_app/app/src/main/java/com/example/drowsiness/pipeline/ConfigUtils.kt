package com.example.drowsiness.pipeline

import kotlin.math.sqrt

/**
 * Config access helpers shared across pipeline stages.
 *
 * All pipeline modules use these to look up values from the parsed YAML
 * config map using the same key paths as the Python code.
 */

@Suppress("UNCHECKED_CAST")
internal fun configSection(config: Map<String, Any>, key: String): Map<String, Any> =
    (config[key] as? Map<String, Any>)
        ?: throw IllegalArgumentException("Missing config section: $key")

@Suppress("UNCHECKED_CAST")
internal fun configSectionOrNull(config: Map<String, Any>?, key: String): Map<String, Any>? =
    (config?.get(key) as? Map<String, Any>)

internal fun configDouble(section: Map<*, *>, key: String, default: Double? = null): Double =
    (section[key] as? Number)?.toDouble()
        ?: default
        ?: throw IllegalArgumentException("Missing config key: $key")

internal fun configInt(section: Map<*, *>, key: String, default: Int? = null): Int =
    (section[key] as? Number)?.toInt()
        ?: default
        ?: throw IllegalArgumentException("Missing config key: $key")

internal fun configBool(section: Map<*, *>, key: String, default: Boolean = false): Boolean =
    section[key] as? Boolean ?: default

internal fun configString(section: Map<*, *>, key: String, default: String? = null): String =
    section[key]?.toString()
        ?: default
        ?: throw IllegalArgumentException("Missing config key: $key")

internal fun configStringOrNull(config: Map<String, Any>?, key: String): String? =
    config?.get(key)?.toString()

/**
 * Median of a DoubleArray.  Returns NaN for empty arrays.
 */
internal fun median(data: DoubleArray): Double {
    if (data.isEmpty()) return Double.NaN
    val sorted = data.sorted()
    val mid = sorted.size / 2
    return if (sorted.size % 2 == 0) (sorted[mid - 1] + sorted[mid]) / 2.0 else sorted[mid]
}

/**
 * Standard deviation (population, ddof=0).
 */
internal fun std(data: DoubleArray): Double {
    if (data.isEmpty()) return 0.0
    val mean = data.average()
    var sum = 0.0
    for (x in data) { val d = x - mean; sum += d * d }
    return sqrt(sum / data.size)
}

/**
 * Content-equality for nullable DoubleArray.
 */
internal fun DoubleArray?.contentEquals(other: DoubleArray?): Boolean {
    if (this == null && other == null) return true
    if (this == null || other == null) return false
    return this.contentEquals(other)
}

/**
 * Content-equality for nullable BooleanArray.
 */
internal fun BooleanArray?.contentEquals(other: BooleanArray?): Boolean {
    if (this == null && other == null) return true
    if (this == null || other == null) return false
    return this.contentEquals(other)
}
