package com.androiddev.data.remote.dto

import com.androiddev.domain.model.NotificationItem
import com.androiddev.domain.model.Notifications

data class NotificationsDto(
    val notifications: List<NotificationDto>,
    val unreadCount:Int
)
fun NotificationsDto.toNotifications(
    notifications: List<NotificationDto>,
    unreadCount: Int
): Notifications {
    return Notifications(
        notifications = notifications.map{
            it.toNotificationItem(
                id = it.id,
                type = it.type,
                content = it.content,
                extrajson = it.extrajson,
                isRead = it.isRead,
                date = it.date
            )
        },
        unreadCount = unreadCount
    )

}