package com.androiddev.snsappwithcompose.feature.PostDetail

import com.androiddev.domain.model.Post
import com.androiddev.snsappwithcompose.common.util.elapsedTime
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_AUDIO
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_IMAGE
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_VIDEO
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.feature.PostDetail.vote.VoteState

data class PostDetailUiState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val voteState: VoteState = VoteState(),
    val isLiked: Boolean = false,
    val errorMessage: UiText? = null
) {
    val displayUserName: UiText
        get() {
            val currentPost = post ?: return UiText.DynamicString("")

            return currentPost.anonymousNickname?.let { anonymous ->
                UiText.StringResource(R.string.anonymous_with_code, anonymous)
            } ?: UiText.DynamicString(currentPost.nickname)
        }

    val tags: List<String>?
        get() = post?.tags?.split('#')?.filter { it.isNotBlank() }

    val elapsedTime: String
        get() = post?.date?.let { elapsedTime(it) } ?: ""
    val mediaUiModel: MediaUiModel
        get() = post?.toMediaUiModel() ?: MediaUiModel(emptyList(), emptyList())
    val audioUrl: String?
        get() = mediaUiModel.audioMedia.firstOrNull()?.url

    val isHeaderLoading: Boolean
        get() {
            // 게시글 자체가 아직 안 들어왔으면 로딩 중
            if (post == null) return true

            // 투표가 존재하는 게시글인데 투표 데이터 로딩 중이면 계속 로딩 중
            // 투표가 없는 일반 게시글이라면 post != null인 시점에 바로 false
            if (post.vote != null && voteState.isLoading) return true

            return false
        }
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