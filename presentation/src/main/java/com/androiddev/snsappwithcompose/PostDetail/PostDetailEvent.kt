package com.androiddev.snsappwithcompose.PostDetail

import android.net.Uri

sealed class PostDetailEvent {
    data class ToggleLikePost(val postId: Int) : PostDetailEvent()
    data class TypeComment(val comment: String): PostDetailEvent()
    data class GotoReplyScreen(val commentId: Int): PostDetailEvent()
    data object PostComment: PostDetailEvent()
    data class ToggleAnonymous(val checked: Boolean): PostDetailEvent()
    data object LoadNextComments: PostDetailEvent()
    data class ToggleLikeComment(val commentId: Int) : PostDetailEvent()
    data class SetCommentSortType(val commentSortType: CommentSortType): PostDetailEvent()
    data class ShowCommentOptions(val myUserId:Int, val commentUserId:Int): PostDetailEvent()
}