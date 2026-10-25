package com.example.drowsiness.logging

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity storing every alert decision with full window context.
 *
 * Every field the decision logger needs is persisted: probability, threshold snapshot,
 * per-channel quality flags, contact continuity, HRV features, and fusion metadata.
 * This enables offline audit of every decision the system made.
 */
@Entity(tableName = "decisions")
data class DecisionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** System timestamp when the decision was made. */
    val timestampMs: Long,

    /** Model output probability (NaN if quality gate failed). */
    val probability: Float,

    /** Whether the drowsiness alert is currently active. */
    val alertActive: Boolean,

    /** Human-readable reason for this decision state. */
    val reason: String,

    /** Snapshot of the alert threshold from config at decision time. */
    val threshold: Float,

    /** Whether the steering-wheel channel passed contact quality. */
    val steeringQualityPassed: Boolean,

    /** Whether the seatbelt channel passed contact quality. */
    val seatbeltQualityPassed: Boolean,

    /** Whether contact was continuous (no handoff) across the window. */
    val continuousContact: Boolean,

    /** Window start timestamp in seconds (ECG timeline). */
    val windowStartS: Double,

    /** Window end timestamp in seconds (ECG timeline). */
    val windowEndS: Double,

    /** RMSSD HRV feature in milliseconds. */
    val rmssdMs: Double,

    /** SDNN HRV feature in milliseconds. */
    val sdnnMs: Double,

    /** pNN50 HRV feature (proportion). */
    val pnn50: Double,

    /** Which channel was selected by fusion (null if neither valid). */
    val selectedChannel: String?,

    /** Whether the two channels disagreed beyond threshold. */
    val fusionDisagreement: Boolean,
)
