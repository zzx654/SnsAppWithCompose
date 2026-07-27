package com.androiddev.snsappwithcompose.common.base.viewmodel

import android.Manifest
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.location.LocationProvider
import com.androiddev.domain.location.LocationState
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.state.PagingUiState
import com.androiddev.snsappwithcompose.common.state.UiEvent
import com.androiddev.snsappwithcompose.common.util.checkPermissions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BasePagingViewModel(
    context:Context,
    private val locationProvider: LocationProvider,
    private val isLocationPermissionRequired: Boolean = false
) : BaseViewModel(context) {


    private val _pagingUiState =
        MutableStateFlow(PagingUiState())

    val pagingUiState =
        _pagingUiState.asStateFlow()

    protected val _locationPermissionGranted = mutableStateOf(true)
    val locationPermissionGranted: State<Boolean>
        get() = _locationPermissionGranted
    private val _location =
        MutableStateFlow(
            LocationState(
                latitude = null,
                longitude = null
            )
        )

    val location: StateFlow<LocationState> = _location.asStateFlow()

    init {
        fetchCurrentLocation()
    }
    fun fetchCurrentLocation() {
        checkPermissions(
            context = context,
            permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            onGranted = {
                _locationPermissionGranted.value = true
                viewModelScope.launch {
                    _location.value = locationProvider.getCurrentLocation()
                }
            },
            onUnGranted = {
                _locationPermissionGranted.value = false
                if (!isLocationPermissionRequired) {
                    _location.value = LocationState(latitude = null, longitude = null)
                }
            }
        )
    }

    fun onClickPostItem(postId:Int) {
        viewModelScope.launch {
            setEvent(
                UiEvent.navigate(
                    Screen.PostDetailScreen(postId)
                )
            )
        }

    }


}