package com.androiddev.data.remote.dto

import com.androiddev.domain.model.NotificationExtra
import com.androiddev.domain.model.NotificationItem
import com.androiddev.domain.util.elapsedTime

data class NotificationDto (
    val id: Long,
    val type: String,
    val content: String,
    val extrajson: NotificationExtra,
    val isread: Int,
    val date: String
)
fun NotificationDto.toNotificationItem(
):NotificationItem {

    return NotificationItem(
        id = id,
        type = type,
        content = content,
        extrajson = extrajson,
        isRead = isread == 1,
        date = date,
        elapsedTime = elapsedTime(date)
    )
}