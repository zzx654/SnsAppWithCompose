package com.androiddev.snsappwithcompose.common.base.viewmodel

import android.Manifest
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.androiddev.snsappwithcompose.feature.home.GetPostsState
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.PostPreview
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.feature.home.events.GetPostsEvent
import com.androiddev.snsappwithcompose.common.navigation.component.Screen

import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.util.checkPermissions
import com.androiddev.snsappwithcompose.common.util.fetchLocation
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch
import com.androiddev.domain.model.Posts
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_AUDIO
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_IMAGE
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_VIDEO
import com.androiddev.snsappwithcompose.feature.home.util.PostPaginator
import dagger.hilt.android.qualifiers.ApplicationContext

abstract class BasePostsViewModel(
    private val isLocationPermissionRequired: Boolean = false,
    @ApplicationContext context: Context,
    protected val locationClient: FusedLocationProviderClient
) : BaseViewModel(context) {


    protected val _getPostState = mutableStateOf(GetPostsState())
    val getPostState: State<GetPostsState> get() = _getPostState

    val uiPosts: List<PostUiState>
        get() = getPostState.value.posts.map { post ->
            PostUiState(
                post = post,
                imageUrls = post.media.filter { it.type == MEDIA_TYPE_IMAGE }.map{ it.url },
                hasVideo = post.media.any { it.type == MEDIA_TYPE_VIDEO },
                hasAudio = post.media.any { it.type == MEDIA_TYPE_AUDIO },
                displayUserName = post.anonymousNickname ?: post.nickname
            )
        }
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
    private fun loadPosts(refresh: Boolean,handleResult: suspend (Resource<Posts>) -> Unit) {
        checkPermissions(
            context = context,
            permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            onGranted = {
                fetchLocation(locationClient) { latitude, longitude ->
                    if (!_locationPermissionGranted.value) _locationPermissionGranted.value = true
                    fetchPostsWithLocation(
                        latitude = latitude,
                        longitude = longitude,
                        refresh = refresh,
                        handleResult = handleResult
                    )
                }
            },
            onUnGranted = {
                _locationPermissionGranted.value = false

                if(!isLocationPermissionRequired) {
                    fetchPostsWithLocation(
                        refresh = refresh,
                        handleResult = handleResult
                    )

                }
            }
        )
    }
    protected abstract fun fetchPostsWithLocation(
        latitude: Double? = null,
        longitude: Double? = null,
        refresh: Boolean,
        handleResult: suspend (Resource<Posts>) -> Unit
    )

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
                viewModelScope.launch {
                    setEvent(
                        UiEvent.navigate(
                            Screen.PostDetailScreen(event.postId)
                        )
                    )
                }
            }
            else -> {
                
            }
        }
    }

}
data class PostUiState(
    val post: PostPreview,
    val imageUrls:List<String>,
    val hasAudio: Boolean,
    val hasVideo: Boolean,
    val displayUserName: String

)