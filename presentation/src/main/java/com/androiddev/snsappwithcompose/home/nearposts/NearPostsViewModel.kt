package com.androiddev.snsappwithcompose.home.nearposts

import android.Manifest
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.use_case.GetPostsUseCases
import com.androiddev.snsappwithcompose.home.GetPostsState
import com.androiddev.snsappwithcompose.util.BaseViewModel
import com.androiddev.snsappwithcompose.util.PostPaginator
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
    private val _getPostState = mutableStateOf(GetPostsState())
    val getPostState: State<GetPostsState>
        get() = _getPostState
    private val _distance = mutableStateOf(5)
    val distance: State<Int>
        get() = _distance
    private val _locationPermissionGranted = mutableStateOf(true)
    val locationPermissionGranted: State<Boolean>
        get() = _locationPermissionGranted

    val postPaginator = PostPaginator(
        loadItems = { handleResult,refresh ->

            checkPermissions(
                context = context,
                permissions = arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                onGranted = {
                    fetchLocation(locationClient) { latitude, longitude ->
                        if(!_locationPermissionGranted.value)
                            _locationPermissionGranted.value = true
                        viewModelScope.launch {
                            var lastPostId:Int? = null
                            var lastPostDate:String? = null
                            with(getPostState.value.posts) {
                                if(isNotEmpty()&&!refresh) {
                                    lastPostDate = last().date
                                    lastPostId = last().postId
                                }
                            }
                                getPostsUseCases.getNearPosts(
                                    postid = lastPostId,
                                    postdate = lastPostDate,
                                    latitude = latitude!!,
                                    longitude = longitude!!,
                                    maxDistance = distance.value
                                ).collect { result ->
                                    handleResult(result)
                                }
                        }
                    }
                },
                onUnGranted = {
                    //권한이 허용되지않았다는 값을 줘야함
                    _locationPermissionGranted.value = false
                }
            )
        },
        onRefreshUpdated = {
            _getPostState.value = _getPostState.value.copy(isRefreshing = it, endReached = false)
        },
        onLoadUpdated = {
                _getPostState.value = _getPostState.value.copy(isLoading = it)


        },
        onError = { message ->
            _getPostState.value = getPostState.value.copy(error = message)
        },
        onSuccess = { posts,refresh ->


            _getPostState.value = getPostState.value.copy(
                posts = if(refresh) posts else getPostState.value.posts + posts,
                endReached = posts.isEmpty() && getPostState.value.posts.isNotEmpty()
            )
        }
    )

    init{
        viewModelScope.launch {
            postPaginator.loadNextItems(refresh = false)
        }
    }
    fun onEvent(event: GetNearPostsEvent) {
        when(event) {
            is GetNearPostsEvent.RefreshNearPosts -> {
                viewModelScope.launch {
                    postPaginator.loadNextItems(refresh = true)
                }

            }
            is GetNearPostsEvent.LoadNextPosts -> {
                viewModelScope.launch {
                    postPaginator.loadNextItems(refresh = false)
                }
            }
            is GetNearPostsEvent.SetDistance -> {
                _distance.value = event.distance
                _getPostState.value = getPostState.value.copy(posts = emptyList())
                viewModelScope.launch {
                    postPaginator.loadNextItems(refresh = false)
                }
            }
            //is GetNearPostsEvent.PermissionChecked -> {
             //   _locationPermissionGranted.value = event.granted
            //}
        }

    }
}