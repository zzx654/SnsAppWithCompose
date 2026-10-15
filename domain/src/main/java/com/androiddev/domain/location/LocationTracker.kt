package com.androiddev.domain.location


interface LocationTracker {
    suspend fun updateLocation(): LocationState
}