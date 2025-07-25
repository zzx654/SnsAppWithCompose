package com.androiddev.snsappwithcompose.PostDetail

import com.androiddev.domain.model.Comment


data class GetCommentsState(
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
)