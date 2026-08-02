package com.androiddev.snsappwithcompose.common.mapper

import com.androiddev.domain.model.Post
import com.androiddev.domain.model.PostPreview
import com.androiddev.snsappwithcompose.common.base.viewmodel.PostUiState
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_AUDIO
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_IMAGE
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_VIDEO

fun PostPreview.toUiState(): PostUiState {

    return PostUiState(
        post = this,
        imageUrls = media
            .filter { it.type == MEDIA_TYPE_IMAGE }
            .map { it.url },

        hasVideo = media.any { it.type == MEDIA_TYPE_VIDEO },

        hasAudio = media.any { it.type == MEDIA_TYPE_AUDIO },

        displayUserName = anonymousNickname ?: nickname
    )
}
fun Post.toUiState(): PostPreviewUiState {

    return PostPreviewUiState(
        post = this,
        imageUrls = media
            .filter { it.type == MEDIA_TYPE_IMAGE }
            .map { it.url },

        hasVideo = media.any { it.type == MEDIA_TYPE_VIDEO },

        hasAudio = media.any { it.type == MEDIA_TYPE_AUDIO },

        displayUserName = anonymousNickname ?: nickname
    )
}
data class PostPreviewUiState(
    val post: Post,
    val imageUrls:List<String>,
    val hasAudio: Boolean,
    val hasVideo: Boolean,
    val displayUserName: String

)