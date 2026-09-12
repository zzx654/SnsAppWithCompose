package com.androiddev.data.remote.dto

import com.androiddev.domain.model.Notification

data class NotificationsDto(
    val notifications: List<NotificationDto>
)
fun NotificationsDto.toDomain(
): List<Notification> {
    return notifications.map{
            it.toDomain(
            )
        }
}