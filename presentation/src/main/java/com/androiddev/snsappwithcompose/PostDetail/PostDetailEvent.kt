package com.androiddev.snsappwithcompose.PostDetail

import android.net.Uri

sealed class PostDetailEvent {
    data class ToggleLikePost(val postid: Int) : PostDetailEvent()
    data class TypeComment(val comment: String): PostDetailEvent()
    data object PostComment: PostDetailEvent()
    data class ToggleAnonymous(val checked: Boolean): PostDetailEvent()
    data object LoadNextComments: PostDetailEvent()
}