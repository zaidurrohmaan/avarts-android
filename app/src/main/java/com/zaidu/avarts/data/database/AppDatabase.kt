package com.zaidu.avarts.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zaidu.avarts.data.database.entities.TrackPoint
import com.zaidu.avarts.data.database.TrackPointDao

@Database(entities = [TrackPoint::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackPointDao(): TrackPointDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "avarts_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}