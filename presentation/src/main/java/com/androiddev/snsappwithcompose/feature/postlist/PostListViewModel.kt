package com.androiddev.snsappwithcompose.feature.postlist

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.androiddev.domain.model.Post
import com.androiddev.domain.model.PostListType
import com.androiddev.domain.use_case.postlist.GetPostListUseCase
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val getPostsUseCase: GetPostListUseCase
) : BaseViewModel() {

    private val _listType = MutableStateFlow<PostListType?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataStream: Flow<PagingData<Post>> = _listType
        .filterNotNull()
        .flatMapLatest { type ->
            getPostsUseCase(type)
        }
        .cachedIn(viewModelScope)

    fun setListType(type: PostListType) {
        if (_listType.value == type) return
        _listType.value = type
    }

    fun onClickPostItem(postId: Int) {
        emitUiEvent(UiEvent.navigate(Screen.PostDetailScreen(postId)))
    }
}