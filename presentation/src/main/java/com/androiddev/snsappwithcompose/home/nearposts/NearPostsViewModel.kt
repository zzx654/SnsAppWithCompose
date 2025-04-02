package com.androiddev.snsappwithcompose.home.nearposts

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.Post
import com.androiddev.domain.use_case.GetPostsUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearPostsViewModel @Inject constructor(
    private val getPostsUseCases: GetPostsUseCases,
    private val context: Context
): BaseViewModel() {
    private val _num = mutableStateOf(0)
    val num: State<Int>
        get() = _num
    private val _isRefreshing = mutableStateOf(false)
    val isRefreshing: State<Boolean>
        get() = _isRefreshing
    private val _distance = mutableStateOf(5)
    val distance: State<Int>
        get() = _distance
    private val _posts = mutableStateOf(emptyList<Post>())
    val posts: State<List<Post>>
        get() = _posts
    fun p() {
        _num.value+=1
    }
    init{
        getPostsUseCases.getNearPosts

    }
    fun refreshPosts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            delay(1000)
            _isRefreshing.value = false
        }

    }
    fun onEvent(event: GetNearPostsEvent) {
        when(event) {
            is GetNearPostsEvent.RefreshNearPosts -> {
                viewModelScope.launch {
                    getPostsUseCases.getNearPosts(
                        latitude = event.latitude,
                        longitude = event.longitude,
                        maxDistance = 5
                    ).collect { result ->
                        when(result) {
                            is Resource.Success -> {
                                result.data?.let {
                                    _posts.value = it.posts
                                }
                            }
                            is Resource.Error -> {
                            }
                            is Resource.Loading -> {
                            }
                        }

                    }
                }

            }
            is GetNearPostsEvent.SetDistance -> {
                _distance.value = event.distance
            }
        }

    }
}