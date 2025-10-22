package com.zaidu.avarts.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_summary")
data class ActivitySummary(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val location: String,
    val activityType: String = "run",
    val title: String,
    val caption: String?,
    val distance: Float,
    val avgPace: Float,
    val movingTime: Long,
    val elapsedTime: Long,
    val elevationGain: Double,
    val splitPace: List<Float>
)