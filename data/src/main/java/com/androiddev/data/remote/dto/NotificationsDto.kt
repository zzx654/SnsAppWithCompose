package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Notifications

data class NotificationsDto(
    val notifications: List<NotificationDto>,
    val unreadCount:Int
)
fun NotificationsDto.toNotifications(
): Notifications {
    return Notifications(
        notifications = notifications.map{
            it.toNotificationItem(
            )
        },
        unreadCount = unreadCount
    )

}