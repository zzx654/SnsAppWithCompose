package com.androiddev.snsappwithcompose.feature.home

import com.androiddev.domain.model.PostPreview

data class GetPostsState(
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val posts: List<PostPreview> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
)