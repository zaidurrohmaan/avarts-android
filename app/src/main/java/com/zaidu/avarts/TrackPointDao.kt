package com.zaidu.avarts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrackPointDao {
    @Insert
    suspend fun insert(trackPoint: TrackPoint)

    @Query("SELECT * FROM track_points ORDER BY time DESC")
    suspend fun getAll(): List<TrackPoint>

    @Query("DELETE FROM track_points")
    suspend fun deleteAll()
}