package com.example.drowsiness.logging;

/**
 * Room DAO for decision log queries.
 *
 * Supports insertion, time-range queries, alert-only queries,
 * and most-recent-N retrieval for UI display.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\n\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u001c\u0010\f\u001a\u00020\u00062\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\n0\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u001c\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\n0\u000e2\u0006\u0010\u0011\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0012J\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\n0\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u0004J$\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\n0\u000e2\u0006\u0010\u0015\u001a\u00020\b2\u0006\u0010\u0016\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\u0017\u00a8\u0006\u0018"}, d2 = {"Lcom/example/drowsiness/logging/DecisionDao;", "", "count", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAll", "", "insert", "", "decision", "Lcom/example/drowsiness/logging/DecisionEntity;", "(Lcom/example/drowsiness/logging/DecisionEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertAll", "decisions", "", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "mostRecent", "n", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "queryAlerts", "queryRange", "startMs", "endMs", "(JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface DecisionDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.example.drowsiness.logging.DecisionEntity decision, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertAll(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.drowsiness.logging.DecisionEntity> decisions, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Query decisions within a timestamp range (inclusive).
     */
    @androidx.room.Query(value = "SELECT * FROM decisions WHERE timestampMs BETWEEN :startMs AND :endMs ORDER BY timestampMs ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object queryRange(long startMs, long endMs, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.drowsiness.logging.DecisionEntity>> $completion);
    
    /**
     * Query only decisions where the alert was active.
     */
    @androidx.room.Query(value = "SELECT * FROM decisions WHERE alertActive = 1 ORDER BY timestampMs ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object queryAlerts(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.drowsiness.logging.DecisionEntity>> $completion);
    
    /**
     * Most recent N decisions, newest first.
     */
    @androidx.room.Query(value = "SELECT * FROM decisions ORDER BY timestampMs DESC LIMIT :n")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object mostRecent(int n, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.drowsiness.logging.DecisionEntity>> $completion);
    
    /**
     * Total count of logged decisions.
     */
    @androidx.room.Query(value = "SELECT COUNT(*) FROM decisions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object count(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    /**
     * Delete all decisions (for testing / session reset).
     */
    @androidx.room.Query(value = "DELETE FROM decisions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}