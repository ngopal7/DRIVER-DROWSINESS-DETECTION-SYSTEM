package com.example.drowsiness.logging

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Room DAO for decision log queries.
 *
 * Supports insertion, time-range queries, alert-only queries,
 * and most-recent-N retrieval for UI display.
 */
@Dao
interface DecisionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(decision: DecisionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(decisions: List<DecisionEntity>)

    /** Query decisions within a timestamp range (inclusive). */
    @Query("SELECT * FROM decisions WHERE timestampMs BETWEEN :startMs AND :endMs ORDER BY timestampMs ASC")
    suspend fun queryRange(startMs: Long, endMs: Long): List<DecisionEntity>

    /** Query only decisions where the alert was active. */
    @Query("SELECT * FROM decisions WHERE alertActive = 1 ORDER BY timestampMs ASC")
    suspend fun queryAlerts(): List<DecisionEntity>

    /** Most recent N decisions, newest first. */
    @Query("SELECT * FROM decisions ORDER BY timestampMs DESC LIMIT :n")
    suspend fun mostRecent(n: Int): List<DecisionEntity>

    /** Total count of logged decisions. */
    @Query("SELECT COUNT(*) FROM decisions")
    suspend fun count(): Int

    /** Delete all decisions (for testing / session reset). */
    @Query("DELETE FROM decisions")
    suspend fun deleteAll()
}
