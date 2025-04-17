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
import com.androiddev.snsappwithcompose.home.GetPostsState
import com.androiddev.snsappwithcompose.util.BaseViewModel
import com.androiddev.snsappwithcompose.util.PostPaginator
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
    private val _getPostState = mutableStateOf(GetPostsState())
    val getPostState: State<GetPostsState>
        get() = _getPostState
    private val _distance = mutableStateOf(5)
    val distance: State<Int>
        get() = _distance
    //private val _posts = mutableStateOf(emptyList<Post>())
    //val posts: State<List<Post>>
     //   get() = _posts
    private val _locationPermissionGranted = mutableStateOf(true)
    val locationPermissionGranted: State<Boolean>
        get() = _locationPermissionGranted

    val postPaginator = PostPaginator(
        loadItems = { handleResult ->
            checkPermissions(
                context = context,
                permissions = arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                onGranted = {
                    fetchLocation(locationClient) { latitude, longitude ->
                        _locationPermissionGranted.value = true
                        viewModelScope.launch {
                            var lastPostId:Int? = null
                            var lastPostDate:String? = null
                            with(getPostState.value.posts) {
                                if(isNotEmpty()) {
                                    lastPostDate = last().date
                                    lastPostId = last().postid
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
        onSuccess = { posts ->
            _getPostState.value = getPostState.value.copy(
                posts = getPostState.value.posts + posts,
                endReached = posts.isEmpty() && getPostState.value.posts.isNotEmpty()
            )
        }


    )

    init{
        postPaginator.loadNextItems(refresh = false)

    }
    fun onEvent(event: GetNearPostsEvent) {
        when(event) {
            is GetNearPostsEvent.RefreshNearPosts -> {
                postPaginator.loadNextItems(refresh = true)

            }
            is GetNearPostsEvent.LoadNextPosts -> {
                postPaginator.loadNextItems(refresh = false)
            }
            is GetNearPostsEvent.SetDistance -> {
                _distance.value = event.distance
                _getPostState.value = getPostState.value.copy(posts = emptyList())
                postPaginator.loadNextItems(refresh = false)
            }
            is GetNearPostsEvent.PermissionChecked -> {
                _locationPermissionGranted.value = event.granted
            }
        }

    }
}