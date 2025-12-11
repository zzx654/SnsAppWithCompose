package com.androiddev.data.remote.dto

import com.androiddev.domain.model.NotificationExtra
import com.androiddev.domain.model.NotificationItem
import com.androiddev.domain.util.elapsedTime

data class NotificationDto (
    val id: Long,
    val type: String,
    val content: String,
    val extrajson: NotificationExtra,
    val isRead: Int,
    val date: String
)
fun NotificationDto.toNotificationItem(
    id: Long,
    type: String,
    content: String,
    extrajson: NotificationExtra,
    isRead: Int,
    date: String
):NotificationItem {

    return NotificationItem(
        id = id,
        type = type,
        content = content,
        extrajson = extrajson,
        isRead = isRead == 1,
        date = date,
        elapsedTime = elapsedTime(date)
    )
}