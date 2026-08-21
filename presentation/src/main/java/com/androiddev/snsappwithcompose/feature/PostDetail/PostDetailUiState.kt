package com.androiddev.snsappwithcompose.feature.PostDetail

import com.androiddev.domain.model.Post
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_AUDIO
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_IMAGE
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_VIDEO
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.feature.PostDetail.vote.VoteState

data class PostDetailUiState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val mediaUiModel: MediaUiModel = post?.toMediaUiModel() ?: MediaUiModel(emptyList(), emptyList()),
    val voteState: VoteState = VoteState(),
    val isLiked: Boolean = false,
    val errorMessage: UiText? = null
) {
    val audioUrl: String?
        get() = mediaUiModel.audioMedia.firstOrNull()?.url
}
fun Post.toMediaUiModel(): MediaUiModel {
    val grouped = media.groupBy { media ->
        when (media.type) {
            MEDIA_TYPE_IMAGE, MEDIA_TYPE_VIDEO -> "VISUAL"
            MEDIA_TYPE_AUDIO -> "AUDIO"
            else -> "OTHER"
        }
    }
    return MediaUiModel(
        visualMedia = grouped["VISUAL"].orEmpty(),
        audioMedia = grouped["AUDIO"].orEmpty()
    )
}