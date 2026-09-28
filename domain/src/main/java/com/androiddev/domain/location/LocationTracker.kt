package com.androiddev.domain.location

import kotlinx.coroutines.flow.StateFlow

interface LocationTracker {
    suspend fun updateLocation(): LocationState
}