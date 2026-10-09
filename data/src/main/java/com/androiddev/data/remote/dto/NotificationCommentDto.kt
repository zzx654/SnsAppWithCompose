package com.androiddev.data.remote.dto

import com.androiddev.domain.model.NotificationComment

data class NotificationCommentDto(
    val comment:CommentDto,
    val reply:CommentDto?
)
fun NotificationCommentDto.toDomain(
): NotificationComment {
    return NotificationComment(
        comment = comment.toDomain(
        ),
        reply = reply?.toDomain(
        )
    )
}