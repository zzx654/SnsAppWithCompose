package com.androiddev.snsappwithcompose.home

import com.androiddev.domain.model.Post

data class GetPostsState(
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
    val lastPostDate: String? = null,
    val lastPostId: Int? = null
)