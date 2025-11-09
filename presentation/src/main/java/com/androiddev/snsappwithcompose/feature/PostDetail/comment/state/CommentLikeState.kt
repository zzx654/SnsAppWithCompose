package com.androiddev.snsappwithcompose.feature.PostDetail.comment.state

data class CommentLikeState(
    val isLiked: Boolean = false,
    val likeCount: Int = 0
)