package com.example.drowsiness.logging

import com.example.drowsiness.pipeline.WindowResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Implements the `LocalDecisionLog` interface from `DrowsinessEngine.kt`,
 * backed by Room for persistent, queryable storage.
 *
 * Enriches the base [AlertDecision] with window quality flags from the
 * feature pipeline. Insertions happen on [Dispatchers.IO] to avoid
 * blocking the calling thread.
 */
class RoomDecisionLog(
    private val dao: DecisionDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
) {
    /**
     * Log a decision with full window context.
     *
     * @param timestampMs   System.currentTimeMillis() at decision time.
     * @param probability   Model output (NaN if quality gate failed).
     * @param alertActive   Whether the alert is currently active.
     * @param reason        Human-readable decision reason.
     * @param threshold     Snapshot of alert threshold from config.
     * @param window        Pipeline window result with quality flags and features.
     */
    fun append(
        timestampMs: Long,
        probability: Float,
        alertActive: Boolean,
        reason: String,
        threshold: Float,
        window: WindowResult?,
    ) {
        val entity = DecisionEntity(
            timestampMs = timestampMs,
            probability = probability,
            alertActive = alertActive,
            reason = reason,
            threshold = threshold,
            steeringQualityPassed = window?.steeringQualityPassed ?: false,
            seatbeltQualityPassed = window?.seatbeltQualityPassed ?: false,
            continuousContact = window?.continuousContact ?: false,
            windowStartS = window?.startS ?: Double.NaN,
            windowEndS = window?.endS ?: Double.NaN,
            rmssdMs = window?.features?.get("rmssd_ms") ?: Double.NaN,
            sdnnMs = window?.features?.get("sdnn_ms") ?: Double.NaN,
            pnn50 = window?.features?.get("pnn50") ?: Double.NaN,
            selectedChannel = window?.selectedChannel?.value,
            fusionDisagreement = window?.fusionDisagreement ?: false,
        )

        scope.launch {
            dao.insert(entity)
        }
    }

    /**
     * Simplified append matching the `LocalDecisionLog.append(AlertDecision)` signature
     * from the existing DrowsinessEngine interface. Window context is unavailable
     * through this path; fields default to NaN/false.
     */
    fun appendSimple(
        timestampMs: Long,
        probability: Float,
        alertActive: Boolean,
        reason: String,
        threshold: Float = Float.NaN,
    ) {
        append(timestampMs, probability, alertActive, reason, threshold, null)
    }
}
