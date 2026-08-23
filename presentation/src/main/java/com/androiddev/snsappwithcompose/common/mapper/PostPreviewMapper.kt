package com.androiddev.snsappwithcompose.common.mapper

import com.androiddev.domain.model.Post
import com.androiddev.domain.model.PostPreview
import com.androiddev.snsappwithcompose.common.util.elapsedTime
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.viewmodel.PostUiState
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_AUDIO
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_IMAGE
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_VIDEO
import com.androiddev.snsappwithcompose.common.util.UiText

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
    val displayUserName = anonymousNickname?.let { nonNullAnonymous ->
    UiText.StringResource(R.string.anonymous_with_code, nonNullAnonymous)
} ?: UiText.DynamicString(nickname)
    return PostPreviewUiState(
        post = this,
        tags = tags?.split('#')?.filter { it.isNotBlank() },
        imageUrls = media
            .filter { it.type == MEDIA_TYPE_IMAGE }
            .map { it.url },

        hasVideo = media.any { it.type == MEDIA_TYPE_VIDEO },

        hasAudio = media.any { it.type == MEDIA_TYPE_AUDIO },

        displayUserName = displayUserName,
        elapsedTime = elapsedTime(this.date)
    )
}
data class PostPreviewUiState(
    val post: Post,
    val tags:List<String>?,
    val elapsedTime:String,
    val imageUrls:List<String>,
    val hasAudio: Boolean,
    val hasVideo: Boolean,
    val displayUserName: UiText

)