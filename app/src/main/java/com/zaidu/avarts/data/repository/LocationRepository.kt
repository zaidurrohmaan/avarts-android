package com.zaidu.avarts.data.repository

import android.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object LocationRepository {

    private val _locationUpdates = MutableStateFlow<Location?>(null)
    val locationUpdates = _locationUpdates.asStateFlow()

    private val _totalDistance = MutableStateFlow(0.0f)
    val totalDistance = _totalDistance.asStateFlow()

    private var lastLocation: Location? = null

    fun addLocation(location: Location) {
        _locationUpdates.value = location
        if (lastLocation != null) {
            _totalDistance.value += location.distanceTo(lastLocation!!)
        }
        lastLocation = location
    }

    fun reset() {
        lastLocation = null
        _locationUpdates.value = null
        _totalDistance.value = 0.0f
    }
}