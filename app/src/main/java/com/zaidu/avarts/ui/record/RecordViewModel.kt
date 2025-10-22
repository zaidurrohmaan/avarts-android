package com.zaidu.avarts.ui.record

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zaidu.avarts.data.database.AppDatabase
import com.zaidu.avarts.data.repository.LocationRepository
import com.zaidu.avarts.service.LocationService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

enum class RecordingState {
    STOPPED,
    RECORDING,
    PAUSED
}

class RecordViewModel(application: Application) : AndroidViewModel(application) {
    var recordingState by mutableStateOf(RecordingState.STOPPED)
        private set

    var hasPermission by mutableStateOf(false)

    var movingTime by mutableStateOf(0L)
    var elapsedTime by mutableStateOf(0L)
    var distance by mutableStateOf(0.0f)
    var splitPace by mutableStateOf(0.0f)
    var averagePace by mutableStateOf(0.0f)

    private val _navigateToSaveActivity = MutableStateFlow(false)
    val navigateToSaveActivity = _navigateToSaveActivity.asStateFlow()

    private val db = AppDatabase.getDatabase(application)
    private var timerJob: Job? = null

    init {
        LocationRepository.locationUpdates.onEach {
            distance = LocationRepository.totalDistance.value
            calculatePaces()
        }.launchIn(viewModelScope)
    }

    fun onStartClick(context: Context) {
        // TODO: Remove this when ready for production
        viewModelScope.launch {
            db.trackPointDao().deleteAll()
        }

        reset()

        if (hasPermission) {
            val intent = Intent(context, LocationService::class.java).apply { action = LocationService.ACTION_START }
            context.startService(intent)
            recordingState = RecordingState.RECORDING
            startTimer()
        } else {
            // TODO: request permission
        }
    }

    fun onPauseClick(context: Context) {
        val intent = Intent(context, LocationService::class.java).apply { action = LocationService.ACTION_PAUSE }
        context.startService(intent)
        recordingState = RecordingState.PAUSED
    }

    fun onResumeClick(context: Context) {
        val intent = Intent(context, LocationService::class.java).apply { action = LocationService.ACTION_RESUME }
        context.startService(intent)
        recordingState = RecordingState.RECORDING
    }

    fun onFinishClick(context: Context) {
        val intent = Intent(context, LocationService::class.java).apply { action = LocationService.ACTION_STOP }
        context.startService(intent)
        recordingState = RecordingState.STOPPED
        stopTimer()
        _navigateToSaveActivity.value = true
    }

    fun onSaveActivityFinished() {
        reset()
        _navigateToSaveActivity.value = false
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                elapsedTime += 1
                if (recordingState == RecordingState.RECORDING) {
                    movingTime += 1
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    private fun calculatePaces() {
        // Calculate split pace (last km)
        // This is a simplified version, a real implementation would be more complex
        if (distance > 0) {
            splitPace = movingTime / (distance / 1000)
        }

        // Calculate average pace
        if (distance > 0) {
            averagePace = movingTime / (distance / 1000)
        }
    }

    private fun reset() {
        LocationRepository.reset()
        movingTime = 0L
        elapsedTime = 0L
        distance = 0.0f
        splitPace = 0.0f
        averagePace = 0.0f
    }
}