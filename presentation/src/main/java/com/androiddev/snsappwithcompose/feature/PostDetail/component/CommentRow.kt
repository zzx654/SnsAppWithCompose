package com.androiddev.snsappwithcompose.feature.PostDetail.component

import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import com.androiddev.domain.model.Comment
import com.androiddev.snsappwithcompose.common.mapper.toUiState
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.component.CommentItem
import com.androiddev.snsappwithcompose.feature.Reply.ReplyItem
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.CommentEvent
@Composable
fun BoundCommentRow(
    showReplyCount:Boolean = true,
    comment: Comment,
    commentStateMap: Map<Int, Comment>,
    userId: Int?,
    imageLoader: ImageLoader,
    onCommentEvent: (CommentEvent) -> Unit = {}
) {
    val updatedComment = commentStateMap[comment.commentId] ?: comment

    if(comment.depth == 0) {
        CommentRow(
            comment = updatedComment,
            imageLoader = imageLoader,
            onLikeClick = {
                onCommentEvent(CommentEvent.ToggleLikeComment(updatedComment))
            },
            onCommentClick = {
                onCommentEvent(CommentEvent.GotoReplyScreen(updatedComment.commentId))
            },
            onOptionClick = {
                userId?.let { my ->
                    onCommentEvent(
                        CommentEvent.ShowCommentOptions(
                            myUserId = my,
                            commentUserId = updatedComment.userId
                        )
                    )
                }
            },
            showReplyCount = showReplyCount
        )

    } else {
        ReplyRow(
            comment = updatedComment,
            imageLoader = imageLoader,
            onLikeClick = {
                onCommentEvent(CommentEvent.ToggleLikeComment(updatedComment))
            },
            onOptionClick = {
                userId?.let { my ->
                    onCommentEvent(
                        CommentEvent.ShowCommentOptions(
                            myUserId = my,
                            commentUserId = updatedComment.userId
                        )
                    )
                }
            }
        )
    }

}
@Composable
fun ReplyRow(
    comment: Comment,
    imageLoader: ImageLoader,
    onLikeClick:()->Unit,
    onOptionClick:() ->Unit
){
    ReplyItem(
        commentUiState = comment.toUiState(),
        imageLoader = imageLoader,
        onLikeClick = onLikeClick,
        onOptionClick = onOptionClick
    )
    Divider(
        color = Color.LightGray,
        thickness = 1.dp
    )
}
@Composable
fun CommentRow(
    comment: Comment,
    imageLoader: ImageLoader,
    onLikeClick:()->Unit,
    onCommentClick:() ->Unit,
    onOptionClick:() ->Unit,
    showReplyCount: Boolean
) {
    CommentItem(
        commentUiState = comment.toUiState(),
        imageLoader = imageLoader,
        onLikeClick = onLikeClick,

        onOptionClick = onOptionClick,
        onCommentClick = onCommentClick,
        showReplyCount = showReplyCount
    )
    Divider(
        color = Color.LightGray,
        thickness = 1.dp
    )
}