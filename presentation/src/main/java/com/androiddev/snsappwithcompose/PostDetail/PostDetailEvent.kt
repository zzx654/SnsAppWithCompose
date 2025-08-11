package com.androiddev.snsappwithcompose.PostDetail

import android.net.Uri

sealed class PostDetailEvent {
    data class ToggleLikePost(val postId: Int) : PostDetailEvent()
}