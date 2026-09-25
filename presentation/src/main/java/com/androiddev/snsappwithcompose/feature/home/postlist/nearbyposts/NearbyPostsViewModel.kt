package com.androiddev.snsappwithcompose.feature.home.postlist.nearbyposts

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.androiddev.domain.model.Post
import com.androiddev.domain.use_case.postlist.GetPostsUseCases
import com.androiddev.snsappwithcompose.feature.home.BasePostsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class NearbyPostsViewModel @Inject constructor(
    private val getPostsUseCases: GetPostsUseCases
) : BasePostsViewModel() {

    private val _selectedRadius = MutableStateFlow(5)
    val selectedRadius: StateFlow<Int> = _selectedRadius.asStateFlow()

    val pagingDataStream: Flow<PagingData<Post>> = selectedRadius
        .flatMapLatest { radius ->
            getPostsUseCases.getNearbyPosts(radius)
        }
        .cachedIn(viewModelScope)
    fun setRadius(radiusKm: Int) {
        _selectedRadius.value = radiusKm
    }
}