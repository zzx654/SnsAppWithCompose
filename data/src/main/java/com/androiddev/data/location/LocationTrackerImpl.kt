package com.androiddev.data.location

import com.androiddev.domain.location.LocationProvider
import com.androiddev.domain.location.LocationState
import com.androiddev.domain.location.LocationTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationTrackerImpl @Inject constructor(
    private val locationProvider: LocationProvider
) : LocationTracker {

    // 앱 전역에서 공유되는 위치 Flow (StateFlow로 유지)
    private val _currentLocation = MutableStateFlow(LocationState(null, null))
    override val currentLocation: StateFlow<LocationState> = _currentLocation.asStateFlow()

    override suspend fun updateLocation() {
        runCatching {
            locationProvider.getCurrentLocation()
        }.onSuccess { freshLocation ->
            _currentLocation.value = freshLocation
        }.onFailure {
            _currentLocation.value = LocationState(latitude = null, longitude = null)
        }
    }

}