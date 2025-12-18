package com.androiddev.snsappwithcompose.feature.home.nearposts

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class NearPostsViewModel @Inject constructor(
    private val getPostsUseCases: GetPostsUseCases,
    locationClient: FusedLocationProviderClient,
    @ApplicationContext context: Context
): BasePostsViewModel(
    context = context,
    locationClient = locationClient,
    isLocationPermissionRequired = true
) {

    private val _distance = mutableStateOf(5)
    val distance: State<Int>
        get() = _distance

    init{
        viewModelScope.launch {
            postPaginator.loadNextItems(refresh = false)
        }
    }

    override fun fetchPostsWithLocation(
        latitude: Double?,
        longitude: Double?,
        refresh: Boolean,
        handleResult: suspend (Resource<Posts>) -> Unit
    ) {
        var lastPostId: Int? = null
        var lastPostDate: String? = null
        with(getPostState.value.posts) {
            if (isNotEmpty() && !refresh) {
                lastPostDate = last().date
                lastPostId = last().postId
            }
        }
        viewModelScope.launch {
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