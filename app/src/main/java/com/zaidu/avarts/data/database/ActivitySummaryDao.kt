package com.zaidu.avarts.data.database

import androidx.room.Dao
import androidx.room.Insert
import com.zaidu.avarts.data.database.entities.ActivitySummary

@Dao
interface ActivitySummaryDao {
    @Insert
    suspend fun insert(activitySummary: ActivitySummary)
}