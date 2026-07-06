package com.androiddev.domain.location

interface LocationProvider {
    suspend fun getCurrentLocation(): LocationState
}