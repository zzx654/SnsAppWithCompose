package com.androiddev.snsappwithcompose.feature.home.postlist.popularposts


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.androiddev.domain.model.Post
import com.androiddev.domain.use_case.postlist.GetPostsUseCases
import com.androiddev.snsappwithcompose.feature.home.BasePostsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PopularPostsViewModel @Inject constructor(
    private val getPostsUseCases:GetPostsUseCases,
    savedStateHandle: SavedStateHandle
) : BasePostsViewModel() {

    // NavArgs 또는 StateHandle에서 tagId 추출 (없으면 null)
    private val tagId: Int? = savedStateHandle.get<Int>("tagId")

    val pagingDataStream: Flow<PagingData<Post>> =
        getPostsUseCases.getRecentPosts(tagId).cachedIn(viewModelScope)//

}