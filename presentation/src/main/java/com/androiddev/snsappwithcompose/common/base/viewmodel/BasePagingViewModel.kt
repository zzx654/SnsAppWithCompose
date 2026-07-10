package com.androiddev.snsappwithcompose.common.base.viewmodel

import android.Manifest
import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.androiddev.domain.exception.ConnectionException
import com.androiddev.domain.exception.TokenExpiredException
import com.androiddev.domain.location.LocationProvider
import com.androiddev.domain.location.LocationState
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.state.PagingUiState
import com.androiddev.snsappwithcompose.common.state.UiEvent
import com.androiddev.snsappwithcompose.common.util.checkPermissions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BasePagingViewModel(
    context:Context,
    private val locationProvider: LocationProvider,
    private val isLocationPermissionRequired: Boolean = false
) : BaseViewModel(context) {


    private val _pagingUiState =
        MutableStateFlow(PagingUiState())

    val pagingUiState =
        _pagingUiState.asStateFlow()


    private val _location =
        MutableStateFlow(
            LocationState(
                latitude = null,
                longitude = null
            )
        )

    val location: StateFlow<LocationState> = _location.asStateFlow()

    init {
        loadLocation()
    }

    private fun loadLocation() {

        checkPermissions(

            context = context,

            permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),

            onGranted = {

                viewModelScope.launch {

                    _location.value =
                        locationProvider.getCurrentLocation()

                }

            },

            onUnGranted = {

                if (!isLocationPermissionRequired) {

                    _location.value = LocationState(
                        latitude = null,
                        longitude = null
                    )
                }

            }
        )
    }

    fun onPagingStateChanged(
        loadStates: CombinedLoadStates
    ) {

        _pagingUiState.update {

            it.copy(

                isRefreshing =
                    loadStates.refresh is LoadState.Loading,

                isAppending =
                    loadStates.append is LoadState.Loading

            )

        }

        val error =

            (loadStates.refresh as? LoadState.Error)?.error
                ?:
                (loadStates.append as? LoadState.Error)?.error

        error?.let {
            viewModelScope.launch {
                onPagingError(it)
            }
        }

    }

    private suspend fun onPagingError(
        throwable: Throwable
    ) {
        when(throwable) {
            is TokenExpiredException -> {
                setEvent(
                    UiEvent.navigate(
                        screen = Screen.SignInScreen
                    )
                )
            }
            is ConnectionException -> {
                setEvent(UiEvent.ShowToast(getString(R.string.connection_error)))
            }
            else -> {
                    setEvent(UiEvent.ShowToast(getString(R.string.error)))
            }
        }
    }
}