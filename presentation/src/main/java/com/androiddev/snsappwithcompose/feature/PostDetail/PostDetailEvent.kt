package com.androiddev.snsappwithcompose.feature.PostDetail

import com.androiddev.domain.model.Post

sealed class PostDetailEvent {
    data class ToggleLikePost(val postId: Long) : PostDetailEvent()
    data object DeletePost : PostDetailEvent()
    data class RequestChat(val myUserId: Long): PostDetailEvent()
    data class LoadEditedPostDetails(val post: Post): PostDetailEvent()
}