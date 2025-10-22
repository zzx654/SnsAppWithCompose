package com.androiddev.snsappwithcompose.base

import android.Manifest
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.androiddev.domain.use_case.GetPostsUseCases
import com.androiddev.snsappwithcompose.home.GetPostsState
import com.androiddev.snsappwithcompose.util.BaseViewModel
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.model.Post
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.home.events.GetNearPostsEvent
import com.androiddev.snsappwithcompose.home.events.GetPostsEvent
import com.androiddev.snsappwithcompose.navigation.components.Screen
import com.androiddev.snsappwithcompose.util.PostPaginator
import com.androiddev.snsappwithcompose.util.UiEvent
import com.androiddev.snsappwithcompose.util.checkPermissions
import com.androiddev.snsappwithcompose.util.fetchLocation
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch

abstract class BasePostsViewModel(
    private val context: Context,
    private val locationClient: FusedLocationProviderClient,
    private val getPostsUseCases: GetPostsUseCases
) : BaseViewModel() {

    protected val _getPostState = mutableStateOf(GetPostsState())
    val getPostState: State<GetPostsState> get() = _getPostState

    protected val _locationPermissionGranted = mutableStateOf(true)
    val locationPermissionGranted: State<Boolean>
        get() = _locationPermissionGranted

    val postPaginator = PostPaginator(
        loadItems = { handleResult, refresh ->
            viewModelScope.launch {
                loadPosts(refresh, handleResult)
            }
        },
        onRefreshUpdated = {
            _getPostState.value = _getPostState.value.copy(isRefreshing = it, endReached = false)
        },
        onLoadUpdated = {
            _getPostState.value = _getPostState.value.copy(isLoading = it)
        },
        onError = { message ->
            _getPostState.value = _getPostState.value.copy(error = message)
        },
        onSuccess = { posts, refresh ->
            _getPostState.value = _getPostState.value.copy(
                posts = if (refresh) posts else getPostState.value.posts + posts,
                endReached = posts.isEmpty() && getPostState.value.posts.isNotEmpty()
            )
        }
    )

    abstract suspend fun loadPosts(refresh: Boolean, handleResult: suspend (Resource<GetPostsResponse>) -> Unit)

    open fun onEvent(event: GetPostsEvent) {
        when (event) {
            is GetPostsEvent.Refresh -> {
                viewModelScope.launch {
                    postPaginator.loadNextItems(refresh = true)
                }
            }
            is GetPostsEvent.LoadNext -> {
                viewModelScope.launch {
                    postPaginator.loadNextItems(refresh = false)
                }
            }
            is GetPostsEvent.SelectPost -> {
                checkPermissions(
                    context = context,
                    permissions = arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION),
                    onGranted = {
                        fetchLocation(locationClient) { latitude, longitude ->
                            if(!_locationPermissionGranted.value)
                                _locationPermissionGranted.value = true
                            getSelectedPost(event.postId,latitude,longitude)
                        }
                    },
                    onUnGranted = {
                        // _locationPermissionGranted.value = false
                        getSelectedPost(event.postId,null,null)
                    }
                )
            }
            else -> {
                
            }
        }
    }

    fun getSelectedPost(postid:Int, latitude:Double?,longitude:Double?) {
        viewModelScope.launch {
            getPostsUseCases.getSelectedPost(postid = postid, latitude = latitude, longitude = longitude)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            setLoading(false)
                            result.data?.let {
                                setEvent(
                                    UiEvent.navigate(
                                        Screen.PostDetailScreen(it.posts[0])
                                    )
                                )

                            }
                        }
                        is Resource.Error -> {
                            setLoading(false)
                        }
                        is Resource.Loading -> {
                            setLoading(true)
                        }
                    }

                }

        }
    }
}