package com.zaidu.avarts.ui.save

import android.app.Application
import android.location.Geocoder
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zaidu.avarts.data.database.AppDatabase
import com.zaidu.avarts.data.database.entities.ActivitySummary
import com.zaidu.avarts.data.database.entities.TrackPoint
import com.zaidu.avarts.util.GpxGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

class SaveViewModel(application: Application) : AndroidViewModel(application) {
    var title by mutableStateOf("")
    var caption by mutableStateOf("")
    var showDiscardDialog by mutableStateOf(false)

    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished.asStateFlow()

    private val db = AppDatabase.getDatabase(application)
    private var trackPoints: List<TrackPoint> = emptyList()

    init {
        viewModelScope.launch {
            trackPoints = db.trackPointDao().getAll().reversed()
        }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            if (trackPoints.isEmpty()) {
                _isFinished.value = true
                return@launch
            }

            val gpxContent = GpxGenerator.generate(trackPoints, title)
            val gpxFile = saveGpxToFile(gpxContent)

            val distance = calculateDistance()
            val movingTime = calculateMovingTime()
            val elapsedTime = calculateElapsedTime()
            val averagePace = calculateAveragePace(distance, movingTime)
            val location = getCityFromLocation(trackPoints.first())

            // TODO: get actual elevation gain and split pace
            val activitySummary = ActivitySummary(
                timestamp = trackPoints.first().time,
                location = location,
                title = title,
                caption = caption,
                distance = distance,
                avgPace = averagePace,
                movingTime = movingTime,
                elapsedTime = elapsedTime,
                elevationGain = 0.0,
                splitPace = listOf(),
                gpxFilePath = gpxFile.absolutePath
            )

            db.activitySummaryDao().insert(activitySummary)
            db.trackPointDao().deleteAll()
            _isFinished.value = true
        }
    }

    fun onDiscardClick() {
        showDiscardDialog = true
    }

    fun onConfirmDiscard() {
        viewModelScope.launch {
            db.trackPointDao().deleteAll()
            _isFinished.value = true
        }
    }

    fun onDismissDiscard() {
        showDiscardDialog = false
    }

    private suspend fun getCityFromLocation(trackPoint: TrackPoint): String {
        val geocoder = Geocoder(getApplication(), Locale.getDefault())
        return try {
            val addresses = withContext(Dispatchers.IO) {
                geocoder.getFromLocation(trackPoint.lat, trackPoint.lon, 1)
            }
            if (addresses?.isNotEmpty() == true) {
                addresses[0]?.locality ?: "Unknown Location"
            } else {
                "Unknown Location"
            }
        } catch (e: Exception) {
            "Unknown Location"
        }
    }

    private fun calculateDistance(): Float {
        var totalDistance = 0f
        for (i in 0 until trackPoints.size - 1) {
            val start = trackPoints[i]
            val end = trackPoints[i + 1]
            if (!start.isPaused && !end.isPaused) {
                val results = FloatArray(1)
                Location.distanceBetween(start.lat, start.lon, end.lat, end.lon, results)
                totalDistance += results[0]
            }
        }
        return totalDistance
    }

    private fun calculateMovingTime(): Long {
        var movingTime = 0L
        for (i in 0 until trackPoints.size - 1) {
            val start = trackPoints[i]
            val end = trackPoints[i + 1]
            if (!start.isPaused) {
                movingTime += (end.time - start.time)
            }
        }
        return movingTime / 1000
    }

    private fun calculateElapsedTime(): Long {
        if (trackPoints.isEmpty()) return 0
        return (trackPoints.last().time - trackPoints.first().time) / 1000
    }

    private fun calculateAveragePace(distance: Float, movingTime: Long): Float {
        if (distance == 0f) return 0f
        return movingTime / (distance / 1000)
    }

    private fun saveGpxToFile(gpxContent: String): File {
        val fileName = "avarts_${System.currentTimeMillis()}.gpx"
        val file = File(getApplication<Application>().filesDir, fileName)
        file.writeText(gpxContent)
        return file
    }
}