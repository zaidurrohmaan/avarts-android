package com.zaidu.avarts.ui.save

import android.app.Application
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zaidu.avarts.data.database.AppDatabase
import com.zaidu.avarts.data.database.entities.ActivitySummary
import com.zaidu.avarts.data.database.entities.TrackPoint
import kotlinx.coroutines.launch

class SaveViewModel(application: Application) : AndroidViewModel(application) {
    var title by mutableStateOf("")
    var caption by mutableStateOf("")
    var showDiscardDialog by mutableStateOf(false)

    private val db = AppDatabase.getDatabase(application)
    private var trackPoints: List<TrackPoint> = emptyList()

    init {
        viewModelScope.launch {
            trackPoints = db.trackPointDao().getAll()
        }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val distance = calculateDistance()
            val movingTime = calculateMovingTime()
            val elapsedTime = calculateElapsedTime()
            val averagePace = calculateAveragePace(distance, movingTime)

            // TODO: Generate GPX file and get actual elevation gain and split pace
            val activitySummary = ActivitySummary(
                timestamp = trackPoints.first().time,
                location = "", // TODO: Get city from first track point
                title = title,
                caption = caption,
                distance = distance,
                avgPace = averagePace,
                movingTime = movingTime,
                elapsedTime = elapsedTime,
                elevationGain = 0.0,
                splitPace = listOf()
            )

            db.activitySummaryDao().insert(activitySummary)
            db.trackPointDao().deleteAll()

            // TODO: Navigate back to fresh RecordActivity
        }
    }

    fun onDiscardClick() {
        showDiscardDialog = true
    }

    fun onConfirmDiscard() {
        viewModelScope.launch {
            db.trackPointDao().deleteAll()
            // TODO: Navigate back to fresh RecordActivity
        }
    }

    fun onDismissDiscard() {
        showDiscardDialog = false
    }

    private fun calculateDistance(): Float {
        var totalDistance = 0f
        for (i in 0 until trackPoints.size - 1) {
            val start = trackPoints[i]
            val end = trackPoints[i + 1]
            val results = FloatArray(1)
            Location.distanceBetween(start.lat, start.lon, end.lat, end.lon, results)
            totalDistance += results[0]
        }
        return totalDistance
    }

    private fun calculateMovingTime(): Long {
        if (trackPoints.isEmpty()) return 0
        return (trackPoints.last().time - trackPoints.first().time) / 1000
    }

    private fun calculateElapsedTime(): Long {
        if (trackPoints.isEmpty()) return 0
        return (trackPoints.last().time - trackPoints.first().time) / 1000
    }

    private fun calculateAveragePace(distance: Float, movingTime: Long): Float {
        if (distance == 0f) return 0f
        return movingTime / (distance / 1000)
    }
}