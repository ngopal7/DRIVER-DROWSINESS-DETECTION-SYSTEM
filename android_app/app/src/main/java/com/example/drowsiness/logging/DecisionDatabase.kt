package com.example.drowsiness.logging

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database singleton for the decision log.
 *
 * Single table: [DecisionEntity]. Version 1.
 * The database file is `drowsiness_decisions.db` in the app's private storage.
 */
@Database(entities = [DecisionEntity::class], version = 1, exportSchema = false)
abstract class DecisionDatabase : RoomDatabase() {

    abstract fun decisionDao(): DecisionDao

    companion object {
        @Volatile
        private var INSTANCE: DecisionDatabase? = null

        fun getInstance(context: Context): DecisionDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DecisionDatabase::class.java,
                    "drowsiness_decisions.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
