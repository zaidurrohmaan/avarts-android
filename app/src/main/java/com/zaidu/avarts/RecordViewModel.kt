package com.zaidu.avarts

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class RecordingState {
    STOPPED,
    RECORDING,
    PAUSED
}

class RecordViewModel : ViewModel() {
    var recordingState by mutableStateOf(RecordingState.STOPPED)
        private set

    var hasPermission by mutableStateOf(false)

    fun onStartClick(context: Context) {
        if (hasPermission) {
            val intent = Intent(context, LocationService::class.java)
            context.startService(intent)
            recordingState = RecordingState.RECORDING
        } else {
            // TODO: request permission
        }
    }

    fun onPauseClick(context: Context) {
        val intent = Intent(context, LocationService::class.java)
        context.stopService(intent)
        recordingState = RecordingState.PAUSED
    }

    fun onResumeClick(context: Context) {
        val intent = Intent(context, LocationService::class.java)
        context.startService(intent)
        recordingState = RecordingState.RECORDING
    }

    fun onFinishClick(context: Context) {
        val intent = Intent(context, LocationService::class.java)
        context.stopService(intent)
        recordingState = RecordingState.STOPPED
        // TODO: navigate to save screen
    }
}