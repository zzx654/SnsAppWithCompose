package com.androiddev.snsappwithcompose.common.base.viewmodel

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.androiddev.domain.exception.ConnectionException
import com.androiddev.domain.exception.TokenExpiredException
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.state.PagingUiState
import com.androiddev.snsappwithcompose.common.state.UiEvent
import com.androiddev.snsappwithcompose.common.util.checkPermissions
import com.androiddev.snsappwithcompose.common.util.fetchLocation
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BasePagingViewModel(
    @ApplicationContext protected val context: Context,
    protected val locationClient: FusedLocationProviderClient,
    private val isLocationPermissionRequired: Boolean = false
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _pagingUiState =
        MutableStateFlow(PagingUiState())

    val pagingUiState =
        _pagingUiState.asStateFlow()

    //private val _event =
    //    MutableSharedFlow<E>()

    //val event =
     //   _event.asSharedFlow()

    /**protected suspend fun setEvent(
        event: E
    ) {
        _event.emit(event)
    }**/
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    protected fun getLatitude() = currentLatitude
    protected fun getLongitude() = currentLongitude

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

                fetchLocation(locationClient) { lat, lon ->


                    currentLatitude = lat
                    currentLongitude = lon
                    println("??????????????????????????????????${currentLatitude},${currentLongitude}")

                }

            },

            onUnGranted = {

                if (!isLocationPermissionRequired) {

                    currentLatitude = null
                    currentLongitude = null

                }

            }

        )
    }
    protected fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }
    suspend fun setEvent(event: UiEvent) = withContext(Dispatchers.Main) {
        _eventFlow.emit(event)
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