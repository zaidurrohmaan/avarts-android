package com.zaidu.avarts.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zaidu.avarts.data.database.entities.ActivitySummary
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivitySummaryDao {
    @Insert
    suspend fun insert(activitySummary: ActivitySummary)

    @Query("SELECT * FROM activity_summary ORDER BY timestamp DESC")
    fun getAllSummaries(): Flow<List<ActivitySummary>>
}