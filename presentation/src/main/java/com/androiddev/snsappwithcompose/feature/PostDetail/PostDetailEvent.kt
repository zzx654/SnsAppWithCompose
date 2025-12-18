package com.androiddev.snsappwithcompose.feature.PostDetail

import android.net.Uri
import com.androiddev.domain.model.PostPreview

sealed class PostDetailEvent {
    data class ToggleLikePost(val postId: Int) : PostDetailEvent()
    data object DeletePost : PostDetailEvent()
    data class LoadEditedPostDetails(val post: PostPreview): PostDetailEvent()
}