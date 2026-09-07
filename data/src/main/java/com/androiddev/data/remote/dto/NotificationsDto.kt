package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Notifications

data class NotificationsDto(
    val notifications: List<NotificationDto>,
    val unreadCount:Int
)
fun NotificationsDto.toDomain(
): Notifications {
    return Notifications(
        notifications = notifications.map{
            it.toDomain(
            )
        },
        unreadCount = unreadCount
    )

}