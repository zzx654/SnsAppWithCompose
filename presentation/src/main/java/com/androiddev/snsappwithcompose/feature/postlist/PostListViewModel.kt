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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val getPostsUseCase: GetPostListUseCase
) : BaseViewModel() {

    private val _listType = MutableStateFlow<PostListType?>(null)

    private val _selectedRadius = MutableStateFlow(5)
    val selectedRadius: StateFlow<Int> = _selectedRadius.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataStream: Flow<PagingData<Post>> = _listType
        .filterNotNull()
        .distinctUntilChanged() // 동일한 Type 재요청 방지
        .flatMapLatest { type ->
            getPostsUseCase(type)
        }
        .cachedIn(viewModelScope) // PagingData Flow 캐싱

    fun setListType(type: PostListType) {
        if (_listType.value == type) return

        if (type is PostListType.Nearby) {
            _selectedRadius.value = type.radiusKm
        }
        _listType.value = type
    }
    fun setRadius(radiusKm: Int) {
        if (_selectedRadius.value == radiusKm && _listType.value is PostListType.Nearby) return
        _selectedRadius.value = radiusKm
        _listType.value = PostListType.Nearby(radiusKm)
    }

    fun onClickPostItem(postId: Int) {
        emitUiEvent(UiEvent.navigate(Screen.PostDetailScreen(postId)))
    }
}