package com.androiddev.snsappwithcompose.feature.PostDetail.comment.state

import com.androiddev.domain.model.Comment


data class GetCommentsState(
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
)