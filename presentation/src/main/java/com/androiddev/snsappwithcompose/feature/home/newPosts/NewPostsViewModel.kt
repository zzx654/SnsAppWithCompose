package com.androiddev.snsappwithcompose.feature.home.newPosts

import android.Manifest
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.use_case.postlist.GetPostsUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.common.base.viewmodel.BasePostsViewModel
import com.androiddev.snsappwithcompose.common.util.checkPermissions
import com.androiddev.snsappwithcompose.common.util.fetchLocation
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPostsViewModel @Inject constructor(
    private val getPostsUseCases: GetPostsUseCases,
    private val locationClient: FusedLocationProviderClient,
    private val context: Context
): BasePostsViewModel(
    getPostsUseCases = getPostsUseCases,
    locationClient = locationClient,
    context = context
) {
    override suspend fun loadPosts(
        refresh: Boolean,
        handleResult: suspend (Resource<GetPostsResponse>) -> Unit
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
                        getPostsUseCases.getNewPosts(
                            postid = lastPostId,
                            postdate = lastPostDate,
                            latitude = latitude!!,
                            longitude = longitude!!,
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

    init {
        viewModelScope.launch {
            postPaginator.loadNextItems(refresh = false)
        }



    }
}