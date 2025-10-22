package com.zaidu.avarts.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_points")
data class TrackPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val lat: Double,
    val lon: Double,
    val time: Long,
    val altitude: Double,
    val isPaused: Boolean = false
)