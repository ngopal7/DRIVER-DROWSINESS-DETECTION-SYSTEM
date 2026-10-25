package com.example.drowsiness.logging;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DecisionDatabase_Impl extends DecisionDatabase {
  private volatile DecisionDao _decisionDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `decisions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestampMs` INTEGER NOT NULL, `probability` REAL NOT NULL, `alertActive` INTEGER NOT NULL, `reason` TEXT NOT NULL, `threshold` REAL NOT NULL, `steeringQualityPassed` INTEGER NOT NULL, `seatbeltQualityPassed` INTEGER NOT NULL, `continuousContact` INTEGER NOT NULL, `windowStartS` REAL NOT NULL, `windowEndS` REAL NOT NULL, `rmssdMs` REAL NOT NULL, `sdnnMs` REAL NOT NULL, `pnn50` REAL NOT NULL, `selectedChannel` TEXT, `fusionDisagreement` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '68069c164fc233c573a406cdb9a148a6')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `decisions`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsDecisions = new HashMap<String, TableInfo.Column>(16);
        _columnsDecisions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecisions.put("timestampMs", new TableInfo.Column("timestampMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecisions.put("probability", new TableInfo.Column("probability", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecisions.put("alertActive", new TableInfo.Column("alertActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecisions.put("reason", new TableInfo.Column("reason", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecisions.put("threshold", new TableInfo.Column("threshold", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecisions.put("steeringQualityPassed", new TableInfo.Column("steeringQualityPassed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecisions.put("seatbeltQualityPassed", new TableInfo.Column("seatbeltQualityPassed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecisions.put("continuousContact", new TableInfo.Column("continuousContact", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecisions.put("windowStartS", new TableInfo.Column("windowStartS", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecisions.put("windowEndS", new TableInfo.Column("windowEndS", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecisions.put("rmssdMs", new TableInfo.Column("rmssdMs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecisions.put("sdnnMs", new TableInfo.Column("sdnnMs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecisions.put("pnn50", new TableInfo.Column("pnn50", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecisions.put("selectedChannel", new TableInfo.Column("selectedChannel", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDecisions.put("fusionDisagreement", new TableInfo.Column("fusionDisagreement", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDecisions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDecisions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDecisions = new TableInfo("decisions", _columnsDecisions, _foreignKeysDecisions, _indicesDecisions);
        final TableInfo _existingDecisions = TableInfo.read(db, "decisions");
        if (!_infoDecisions.equals(_existingDecisions)) {
          return new RoomOpenHelper.ValidationResult(false, "decisions(com.example.drowsiness.logging.DecisionEntity).\n"
                  + " Expected:\n" + _infoDecisions + "\n"
                  + " Found:\n" + _existingDecisions);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "68069c164fc233c573a406cdb9a148a6", "d2f6ad0fb3ba82977c2d82df7e704672");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "decisions");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `decisions`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(DecisionDao.class, DecisionDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public DecisionDao decisionDao() {
    if (_decisionDao != null) {
      return _decisionDao;
    } else {
      synchronized(this) {
        if(_decisionDao == null) {
          _decisionDao = new DecisionDao_Impl(this);
        }
        return _decisionDao;
      }
    }
  }
}
