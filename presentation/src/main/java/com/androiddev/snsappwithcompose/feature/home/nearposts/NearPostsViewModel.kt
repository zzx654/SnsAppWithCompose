package com.androiddev.snsappwithcompose.feature.home.nearposts

import android.Manifest
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.Posts
import com.androiddev.domain.use_case.postlist.GetPostsUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.common.base.viewmodel.BasePostsViewModel
import com.androiddev.snsappwithcompose.feature.home.events.GetNearPostsEvent
import com.androiddev.snsappwithcompose.feature.home.events.GetPostsEvent
import com.androiddev.snsappwithcompose.common.util.checkPermissions
import com.androiddev.snsappwithcompose.common.util.fetchLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.android.gms.location.FusedLocationProviderClient

@HiltViewModel
class NearPostsViewModel @Inject constructor(
    private val getPostsUseCases: GetPostsUseCases,
    private val locationClient: FusedLocationProviderClient,
    private val context: Context
): BasePostsViewModel(
    getPostsUseCases = getPostsUseCases,
    locationClient = locationClient,
    context = context
) {

    private val _distance = mutableStateOf(5)
    val distance: State<Int>
        get() = _distance


    override suspend fun loadPosts(
        refresh: Boolean,
        handleResult: suspend (Resource<Posts>) -> Unit
    ) {
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
    }

    init{
        viewModelScope.launch {
            postPaginator.loadNextItems(refresh = false)
        }
    }

    override fun onEvent(event: GetPostsEvent) {
        super.onEvent(event)
        when(event) {
            is GetNearPostsEvent.SetDistance -> {
                _distance.value = event.distance
                _getPostState.value = getPostState.value.copy(posts = emptyList())
                viewModelScope.launch {
                    postPaginator.loadNextItems(refresh = false)
                }
            }
            else -> {
            }
        }
    }
}