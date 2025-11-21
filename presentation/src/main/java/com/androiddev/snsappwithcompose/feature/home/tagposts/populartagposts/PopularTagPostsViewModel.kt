package com.androiddev.snsappwithcompose.feature.home.tagposts.populartagposts

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
class PopularTagPostsViewModel @Inject constructor(
    private val getPostsUseCases: GetPostsUseCases,
    locationClient: FusedLocationProviderClient,
    @ApplicationContext context: Context,
): BasePostsViewModel(
    getPostsUseCases = getPostsUseCases,
    context = context,
    locationClient = locationClient
) {
    private val _tagId = mutableStateOf(5)
    val tagId: State<Int>
        get() = _tagId
    fun initTagPosts(tagId:Int) {
        _tagId.value = tagId
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
        var lastPostId:Int? = null
        var lastPostScore:Double? = null
        with(getPostState.value.posts) {
            if(isNotEmpty()&&!refresh) {
                lastPostScore = last().popularityScore
                lastPostId = last().postId
            }
        }
        viewModelScope.launch {
            getPostsUseCases.getPopularTagPosts(
                postId = lastPostId,
                tagId = tagId.value,
                score = lastPostScore,
                latitude = latitude,
                longitude = longitude,
            ).collect { result ->
                handleResult(result)
            }
        }
    }
}