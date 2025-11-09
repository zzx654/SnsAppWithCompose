package com.androiddev.snsappwithcompose.feature.PostDetail

import android.net.Uri

sealed class PostDetailEvent {
    data class ToggleLikePost(val postId: Int) : PostDetailEvent()
    data object DeletePost : PostDetailEvent()
}