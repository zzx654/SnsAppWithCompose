package com.androiddev.snsappwithcompose.feature.home.tagposts.populartagposts

import android.Manifest
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
class PopularTagPostsViewModel @Inject constructor(
    private val getPostsUseCases: GetPostsUseCases,
    private val locationClient: FusedLocationProviderClient,
    private val context: Context
): BasePostsViewModel(
    getPostsUseCases = getPostsUseCases,
    locationClient = locationClient,
    context = context
) {
    private val _tagId = mutableStateOf(5)
    val tagId: State<Int>
        get() = _tagId
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
                        var lastPostScore:Double? = null
                        with(getPostState.value.posts) {
                            if(isNotEmpty()&&!refresh) {
                                lastPostScore = last().popularityScore
                                lastPostId = last().postId
                            }
                        }
                        getPostsUseCases.getPopularTagPosts(
                            postId = lastPostId,
                            tagId = tagId.value,
                            score = lastPostScore,
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
    fun initTagPosts(tagId:Int) {
        _tagId.value = tagId
        viewModelScope.launch {
            postPaginator.loadNextItems(refresh = false)
        }
    }
}