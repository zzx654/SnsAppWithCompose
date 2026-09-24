package com.androiddev.snsappwithcompose.feature.home.postlist.recentposts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.androiddev.domain.model.Post
import com.androiddev.domain.use_case.postlist.GetPostsUseCases
import com.androiddev.snsappwithcompose.feature.home.BasePostsViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecentPostsViewModel @Inject constructor(
    private val getPostsUseCases:GetPostsUseCases,
    savedStateHandle: SavedStateHandle
) : BasePostsViewModel() {

    // NavArgs 또는 StateHandle에서 tagId 추출 (없으면 null)
    private val tagId: Int? = savedStateHandle.get<Int>("tagId")

    val pagingDataStream: Flow<PagingData<Post>> =
        getPostsUseCases.getRecentPosts(tagId).cachedIn(viewModelScope)

}