package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Notification
import com.androiddev.domain.model.NotificationExtra

data class NotificationDto (
    val id: Long,
    val type: String,
    val content: String,
    val extrajson: NotificationExtra,
    val isread: Int,
    val date: String
)
fun NotificationDto.toDomain(
): Notification {

    return Notification(
        id = id,
        type = type,
        content = content,
        extrajson = extrajson,
        isRead = isread == 1,
        date = date
    )
}