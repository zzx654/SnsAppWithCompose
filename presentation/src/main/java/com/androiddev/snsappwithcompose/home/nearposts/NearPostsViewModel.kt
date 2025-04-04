package com.androiddev.snsappwithcompose.home.nearposts

import android.Manifest
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.Post
import com.androiddev.domain.use_case.GetPostsUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.BaseViewModel
import com.androiddev.snsappwithcompose.util.UiEvent
import com.androiddev.snsappwithcompose.util.checkPermissions
import com.androiddev.snsappwithcompose.util.fetchLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.android.gms.location.FusedLocationProviderClient

@HiltViewModel
class NearPostsViewModel @Inject constructor(
    private val getPostsUseCases: GetPostsUseCases,
    private val locationClient: FusedLocationProviderClient,
    private val context: Context
): BaseViewModel() {
    private val _isRefreshing = mutableStateOf(false)
    val isRefreshing: State<Boolean>
        get() = _isRefreshing
    private val _distance = mutableStateOf(5)
    val distance: State<Int>
        get() = _distance
    private val _posts = mutableStateOf(emptyList<Post>())
    val posts: State<List<Post>>
        get() = _posts
    private val _locationPermissionGranted = mutableStateOf(true)
    val locationPermissionGranted: State<Boolean>
        get() = _locationPermissionGranted


    fun getNearPosts() {
        checkPermissions(
            context = context,
            permissions = arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
            onGranted = {
                fetchLocation(locationClient) { latitude,longitude ->
                    _locationPermissionGranted.value = true
                    viewModelScope.launch {
                        getPostsUseCases.getNearPosts(
                            latitude = latitude!!,
                            longitude = longitude!!,
                            maxDistance = distance.value
                        ).collect { result ->
                            when(result) {
                                is Resource.Success -> {
                                    setLoading(false)
                                    result.data?.let {
                                        _posts.value = it.posts
                                    }
                                }
                                is Resource.Error -> {
                                    setLoading(false)
                                    setEvent(
                                        UiEvent.ShowToast(
                                            message = result.message ?: getString(context,R.string.error)
                                        )
                                    )
                                }
                                is Resource.Loading -> {
                                    setLoading(true)
                                }
                            }
                        }
                    }
                }
            },
            onUnGranted = {
                //권한이 허용되지않았다는 값을 줘야함
                _locationPermissionGranted.value = false
            }
        )
    }
    init{
        getNearPosts()

    }
    fun onEvent(event: GetNearPostsEvent) {
        when(event) {
            is GetNearPostsEvent.RefreshNearPosts -> {
                getNearPosts()

            }
            is GetNearPostsEvent.SetDistance -> {
                _distance.value = event.distance
                _posts.value = listOf()
                getNearPosts()
            }
            is GetNearPostsEvent.PermissionChecked -> {
                _locationPermissionGranted.value = event.granted
            }
        }

    }
}