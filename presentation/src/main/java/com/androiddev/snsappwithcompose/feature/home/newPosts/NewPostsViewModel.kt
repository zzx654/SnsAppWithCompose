package com.androiddev.snsappwithcompose.feature.home.newPosts

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.Posts
import com.androiddev.domain.use_case.postlist.GetPostsUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.common.base.viewmodel.BasePostsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPostsViewModel @Inject constructor(
    private val getPostsUseCases: GetPostsUseCases,
    locationClient: FusedLocationProviderClient,
    @ApplicationContext context: Context
): BasePostsViewModel(
    context = context,
    locationClient = locationClient
) {

    init {
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
            getPostsUseCases.getNewPosts(
                postid = lastPostId,
                postdate = lastPostDate,
                latitude = latitude,
                longitude = longitude,
            ).collect { result ->
                handleResult(result)
            }
        }
    }
}