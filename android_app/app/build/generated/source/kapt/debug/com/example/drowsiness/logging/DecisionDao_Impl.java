package com.example.drowsiness.logging;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DecisionDao_Impl implements DecisionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DecisionEntity> __insertionAdapterOfDecisionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public DecisionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDecisionEntity = new EntityInsertionAdapter<DecisionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `decisions` (`id`,`timestampMs`,`probability`,`alertActive`,`reason`,`threshold`,`steeringQualityPassed`,`seatbeltQualityPassed`,`continuousContact`,`windowStartS`,`windowEndS`,`rmssdMs`,`sdnnMs`,`pnn50`,`selectedChannel`,`fusionDisagreement`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DecisionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTimestampMs());
        statement.bindDouble(3, entity.getProbability());
        final int _tmp = entity.getAlertActive() ? 1 : 0;
        statement.bindLong(4, _tmp);
        if (entity.getReason() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getReason());
        }
        statement.bindDouble(6, entity.getThreshold());
        final int _tmp_1 = entity.getSteeringQualityPassed() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        final int _tmp_2 = entity.getSeatbeltQualityPassed() ? 1 : 0;
        statement.bindLong(8, _tmp_2);
        final int _tmp_3 = entity.getContinuousContact() ? 1 : 0;
        statement.bindLong(9, _tmp_3);
        statement.bindDouble(10, entity.getWindowStartS());
        statement.bindDouble(11, entity.getWindowEndS());
        statement.bindDouble(12, entity.getRmssdMs());
        statement.bindDouble(13, entity.getSdnnMs());
        statement.bindDouble(14, entity.getPnn50());
        if (entity.getSelectedChannel() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getSelectedChannel());
        }
        final int _tmp_4 = entity.getFusionDisagreement() ? 1 : 0;
        statement.bindLong(16, _tmp_4);
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM decisions";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final DecisionEntity decision,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDecisionEntity.insertAndReturnId(decision);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<DecisionEntity> decisions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDecisionEntity.insert(decisions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object queryRange(final long startMs, final long endMs,
      final Continuation<? super List<DecisionEntity>> $completion) {
    final String _sql = "SELECT * FROM decisions WHERE timestampMs BETWEEN ? AND ? ORDER BY timestampMs ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startMs);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endMs);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DecisionEntity>>() {
      @Override
      @NonNull
      public List<DecisionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestampMs = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMs");
          final int _cursorIndexOfProbability = CursorUtil.getColumnIndexOrThrow(_cursor, "probability");
          final int _cursorIndexOfAlertActive = CursorUtil.getColumnIndexOrThrow(_cursor, "alertActive");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "threshold");
          final int _cursorIndexOfSteeringQualityPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "steeringQualityPassed");
          final int _cursorIndexOfSeatbeltQualityPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "seatbeltQualityPassed");
          final int _cursorIndexOfContinuousContact = CursorUtil.getColumnIndexOrThrow(_cursor, "continuousContact");
          final int _cursorIndexOfWindowStartS = CursorUtil.getColumnIndexOrThrow(_cursor, "windowStartS");
          final int _cursorIndexOfWindowEndS = CursorUtil.getColumnIndexOrThrow(_cursor, "windowEndS");
          final int _cursorIndexOfRmssdMs = CursorUtil.getColumnIndexOrThrow(_cursor, "rmssdMs");
          final int _cursorIndexOfSdnnMs = CursorUtil.getColumnIndexOrThrow(_cursor, "sdnnMs");
          final int _cursorIndexOfPnn50 = CursorUtil.getColumnIndexOrThrow(_cursor, "pnn50");
          final int _cursorIndexOfSelectedChannel = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedChannel");
          final int _cursorIndexOfFusionDisagreement = CursorUtil.getColumnIndexOrThrow(_cursor, "fusionDisagreement");
          final List<DecisionEntity> _result = new ArrayList<DecisionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DecisionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTimestampMs;
            _tmpTimestampMs = _cursor.getLong(_cursorIndexOfTimestampMs);
            final float _tmpProbability;
            _tmpProbability = _cursor.getFloat(_cursorIndexOfProbability);
            final boolean _tmpAlertActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAlertActive);
            _tmpAlertActive = _tmp != 0;
            final String _tmpReason;
            if (_cursor.isNull(_cursorIndexOfReason)) {
              _tmpReason = null;
            } else {
              _tmpReason = _cursor.getString(_cursorIndexOfReason);
            }
            final float _tmpThreshold;
            _tmpThreshold = _cursor.getFloat(_cursorIndexOfThreshold);
            final boolean _tmpSteeringQualityPassed;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfSteeringQualityPassed);
            _tmpSteeringQualityPassed = _tmp_1 != 0;
            final boolean _tmpSeatbeltQualityPassed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfSeatbeltQualityPassed);
            _tmpSeatbeltQualityPassed = _tmp_2 != 0;
            final boolean _tmpContinuousContact;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfContinuousContact);
            _tmpContinuousContact = _tmp_3 != 0;
            final double _tmpWindowStartS;
            _tmpWindowStartS = _cursor.getDouble(_cursorIndexOfWindowStartS);
            final double _tmpWindowEndS;
            _tmpWindowEndS = _cursor.getDouble(_cursorIndexOfWindowEndS);
            final double _tmpRmssdMs;
            _tmpRmssdMs = _cursor.getDouble(_cursorIndexOfRmssdMs);
            final double _tmpSdnnMs;
            _tmpSdnnMs = _cursor.getDouble(_cursorIndexOfSdnnMs);
            final double _tmpPnn50;
            _tmpPnn50 = _cursor.getDouble(_cursorIndexOfPnn50);
            final String _tmpSelectedChannel;
            if (_cursor.isNull(_cursorIndexOfSelectedChannel)) {
              _tmpSelectedChannel = null;
            } else {
              _tmpSelectedChannel = _cursor.getString(_cursorIndexOfSelectedChannel);
            }
            final boolean _tmpFusionDisagreement;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfFusionDisagreement);
            _tmpFusionDisagreement = _tmp_4 != 0;
            _item = new DecisionEntity(_tmpId,_tmpTimestampMs,_tmpProbability,_tmpAlertActive,_tmpReason,_tmpThreshold,_tmpSteeringQualityPassed,_tmpSeatbeltQualityPassed,_tmpContinuousContact,_tmpWindowStartS,_tmpWindowEndS,_tmpRmssdMs,_tmpSdnnMs,_tmpPnn50,_tmpSelectedChannel,_tmpFusionDisagreement);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object queryAlerts(final Continuation<? super List<DecisionEntity>> $completion) {
    final String _sql = "SELECT * FROM decisions WHERE alertActive = 1 ORDER BY timestampMs ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DecisionEntity>>() {
      @Override
      @NonNull
      public List<DecisionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestampMs = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMs");
          final int _cursorIndexOfProbability = CursorUtil.getColumnIndexOrThrow(_cursor, "probability");
          final int _cursorIndexOfAlertActive = CursorUtil.getColumnIndexOrThrow(_cursor, "alertActive");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "threshold");
          final int _cursorIndexOfSteeringQualityPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "steeringQualityPassed");
          final int _cursorIndexOfSeatbeltQualityPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "seatbeltQualityPassed");
          final int _cursorIndexOfContinuousContact = CursorUtil.getColumnIndexOrThrow(_cursor, "continuousContact");
          final int _cursorIndexOfWindowStartS = CursorUtil.getColumnIndexOrThrow(_cursor, "windowStartS");
          final int _cursorIndexOfWindowEndS = CursorUtil.getColumnIndexOrThrow(_cursor, "windowEndS");
          final int _cursorIndexOfRmssdMs = CursorUtil.getColumnIndexOrThrow(_cursor, "rmssdMs");
          final int _cursorIndexOfSdnnMs = CursorUtil.getColumnIndexOrThrow(_cursor, "sdnnMs");
          final int _cursorIndexOfPnn50 = CursorUtil.getColumnIndexOrThrow(_cursor, "pnn50");
          final int _cursorIndexOfSelectedChannel = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedChannel");
          final int _cursorIndexOfFusionDisagreement = CursorUtil.getColumnIndexOrThrow(_cursor, "fusionDisagreement");
          final List<DecisionEntity> _result = new ArrayList<DecisionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DecisionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTimestampMs;
            _tmpTimestampMs = _cursor.getLong(_cursorIndexOfTimestampMs);
            final float _tmpProbability;
            _tmpProbability = _cursor.getFloat(_cursorIndexOfProbability);
            final boolean _tmpAlertActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAlertActive);
            _tmpAlertActive = _tmp != 0;
            final String _tmpReason;
            if (_cursor.isNull(_cursorIndexOfReason)) {
              _tmpReason = null;
            } else {
              _tmpReason = _cursor.getString(_cursorIndexOfReason);
            }
            final float _tmpThreshold;
            _tmpThreshold = _cursor.getFloat(_cursorIndexOfThreshold);
            final boolean _tmpSteeringQualityPassed;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfSteeringQualityPassed);
            _tmpSteeringQualityPassed = _tmp_1 != 0;
            final boolean _tmpSeatbeltQualityPassed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfSeatbeltQualityPassed);
            _tmpSeatbeltQualityPassed = _tmp_2 != 0;
            final boolean _tmpContinuousContact;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfContinuousContact);
            _tmpContinuousContact = _tmp_3 != 0;
            final double _tmpWindowStartS;
            _tmpWindowStartS = _cursor.getDouble(_cursorIndexOfWindowStartS);
            final double _tmpWindowEndS;
            _tmpWindowEndS = _cursor.getDouble(_cursorIndexOfWindowEndS);
            final double _tmpRmssdMs;
            _tmpRmssdMs = _cursor.getDouble(_cursorIndexOfRmssdMs);
            final double _tmpSdnnMs;
            _tmpSdnnMs = _cursor.getDouble(_cursorIndexOfSdnnMs);
            final double _tmpPnn50;
            _tmpPnn50 = _cursor.getDouble(_cursorIndexOfPnn50);
            final String _tmpSelectedChannel;
            if (_cursor.isNull(_cursorIndexOfSelectedChannel)) {
              _tmpSelectedChannel = null;
            } else {
              _tmpSelectedChannel = _cursor.getString(_cursorIndexOfSelectedChannel);
            }
            final boolean _tmpFusionDisagreement;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfFusionDisagreement);
            _tmpFusionDisagreement = _tmp_4 != 0;
            _item = new DecisionEntity(_tmpId,_tmpTimestampMs,_tmpProbability,_tmpAlertActive,_tmpReason,_tmpThreshold,_tmpSteeringQualityPassed,_tmpSeatbeltQualityPassed,_tmpContinuousContact,_tmpWindowStartS,_tmpWindowEndS,_tmpRmssdMs,_tmpSdnnMs,_tmpPnn50,_tmpSelectedChannel,_tmpFusionDisagreement);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object mostRecent(final int n,
      final Continuation<? super List<DecisionEntity>> $completion) {
    final String _sql = "SELECT * FROM decisions ORDER BY timestampMs DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, n);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DecisionEntity>>() {
      @Override
      @NonNull
      public List<DecisionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestampMs = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMs");
          final int _cursorIndexOfProbability = CursorUtil.getColumnIndexOrThrow(_cursor, "probability");
          final int _cursorIndexOfAlertActive = CursorUtil.getColumnIndexOrThrow(_cursor, "alertActive");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "threshold");
          final int _cursorIndexOfSteeringQualityPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "steeringQualityPassed");
          final int _cursorIndexOfSeatbeltQualityPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "seatbeltQualityPassed");
          final int _cursorIndexOfContinuousContact = CursorUtil.getColumnIndexOrThrow(_cursor, "continuousContact");
          final int _cursorIndexOfWindowStartS = CursorUtil.getColumnIndexOrThrow(_cursor, "windowStartS");
          final int _cursorIndexOfWindowEndS = CursorUtil.getColumnIndexOrThrow(_cursor, "windowEndS");
          final int _cursorIndexOfRmssdMs = CursorUtil.getColumnIndexOrThrow(_cursor, "rmssdMs");
          final int _cursorIndexOfSdnnMs = CursorUtil.getColumnIndexOrThrow(_cursor, "sdnnMs");
          final int _cursorIndexOfPnn50 = CursorUtil.getColumnIndexOrThrow(_cursor, "pnn50");
          final int _cursorIndexOfSelectedChannel = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedChannel");
          final int _cursorIndexOfFusionDisagreement = CursorUtil.getColumnIndexOrThrow(_cursor, "fusionDisagreement");
          final List<DecisionEntity> _result = new ArrayList<DecisionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DecisionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTimestampMs;
            _tmpTimestampMs = _cursor.getLong(_cursorIndexOfTimestampMs);
            final float _tmpProbability;
            _tmpProbability = _cursor.getFloat(_cursorIndexOfProbability);
            final boolean _tmpAlertActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAlertActive);
            _tmpAlertActive = _tmp != 0;
            final String _tmpReason;
            if (_cursor.isNull(_cursorIndexOfReason)) {
              _tmpReason = null;
            } else {
              _tmpReason = _cursor.getString(_cursorIndexOfReason);
            }
            final float _tmpThreshold;
            _tmpThreshold = _cursor.getFloat(_cursorIndexOfThreshold);
            final boolean _tmpSteeringQualityPassed;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfSteeringQualityPassed);
            _tmpSteeringQualityPassed = _tmp_1 != 0;
            final boolean _tmpSeatbeltQualityPassed;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfSeatbeltQualityPassed);
            _tmpSeatbeltQualityPassed = _tmp_2 != 0;
            final boolean _tmpContinuousContact;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfContinuousContact);
            _tmpContinuousContact = _tmp_3 != 0;
            final double _tmpWindowStartS;
            _tmpWindowStartS = _cursor.getDouble(_cursorIndexOfWindowStartS);
            final double _tmpWindowEndS;
            _tmpWindowEndS = _cursor.getDouble(_cursorIndexOfWindowEndS);
            final double _tmpRmssdMs;
            _tmpRmssdMs = _cursor.getDouble(_cursorIndexOfRmssdMs);
            final double _tmpSdnnMs;
            _tmpSdnnMs = _cursor.getDouble(_cursorIndexOfSdnnMs);
            final double _tmpPnn50;
            _tmpPnn50 = _cursor.getDouble(_cursorIndexOfPnn50);
            final String _tmpSelectedChannel;
            if (_cursor.isNull(_cursorIndexOfSelectedChannel)) {
              _tmpSelectedChannel = null;
            } else {
              _tmpSelectedChannel = _cursor.getString(_cursorIndexOfSelectedChannel);
            }
            final boolean _tmpFusionDisagreement;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfFusionDisagreement);
            _tmpFusionDisagreement = _tmp_4 != 0;
            _item = new DecisionEntity(_tmpId,_tmpTimestampMs,_tmpProbability,_tmpAlertActive,_tmpReason,_tmpThreshold,_tmpSteeringQualityPassed,_tmpSeatbeltQualityPassed,_tmpContinuousContact,_tmpWindowStartS,_tmpWindowEndS,_tmpRmssdMs,_tmpSdnnMs,_tmpPnn50,_tmpSelectedChannel,_tmpFusionDisagreement);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM decisions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
