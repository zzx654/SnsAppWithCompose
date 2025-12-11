package com.androiddev.data.remote.dto

import com.androiddev.domain.model.NotificationItem

data class NotificationsDto(
    val notifications: List<NotificationDto>
)
fun NotificationsDto.toNotifications(
    notifications: List<NotificationDto>
):List<NotificationItem> {
    return notifications.map{
        it.toNotificationItem(
            id = it.id,
            type = it.type,
            content = it.content,
            extrajson = it.extrajson,
            isRead = it.isRead,
            date = it.date
        )
    }
}