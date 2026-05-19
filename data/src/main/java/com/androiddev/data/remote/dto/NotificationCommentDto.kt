package com.androiddev.data.remote.dto

import com.androiddev.domain.model.NotificationComment

data class NotificationCommentDto(
    val comment:CommentDto,
    val reply:CommentDto?
)
fun NotificationCommentDto.toNotificationComment(
): NotificationComment {
    return NotificationComment(
        comment = comment.toComment(
        ),
        reply = reply?.toComment(
        )
    )
}