package com.zaidu.avarts.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zaidu.avarts.data.database.entities.TrackPoint

@Dao
interface TrackPointDao {
    @Insert
    suspend fun insert(trackPoint: TrackPoint)

    @Query("SELECT * FROM track_points ORDER BY time DESC")
    suspend fun getAll(): List<TrackPoint>

    @Query("DELETE FROM track_points")
    suspend fun deleteAll()
}