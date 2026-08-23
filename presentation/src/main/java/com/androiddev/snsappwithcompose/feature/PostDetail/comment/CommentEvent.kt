package com.androiddev.snsappwithcompose.feature.PostDetail.comment

import com.androiddev.domain.model.Comment
import com.androiddev.domain.model.CommentSortType


sealed class CommentEvent {
    data class TypeComment(val comment: String): CommentEvent()
    data object PostComment: CommentEvent()
    data object PostReply: CommentEvent()
    data class ToggleAnonymous(val checked: Boolean): CommentEvent()
    data class ToggleLikeComment(val comment: Comment) : CommentEvent()
    data class ShowCommentOptions(val myUserId:Int, val commentUserId:Int): CommentEvent()
    data class SetCommentSortType(val commentSortType: CommentSortType): CommentEvent()
    data class GotoReplyScreen(val commentId: Int): CommentEvent()
}