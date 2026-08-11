package com.androiddev.domain.location

import kotlinx.coroutines.flow.StateFlow

interface LocationTracker {
    val currentLocation: StateFlow<LocationState>
    suspend fun updateLocation()
}