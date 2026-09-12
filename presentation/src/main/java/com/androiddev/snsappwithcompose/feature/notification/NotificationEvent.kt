package com.androiddev.snsappwithcompose.feature.notification

import com.androiddev.domain.model.Notification


sealed class NotificationEvent {
    data class DeleteNotifications(val targetMaxId: Long? = null): NotificationEvent()
    data class ReadAllNotifications(val targetMaxId:Long? = null): NotificationEvent()
    data class ReadNotification(val notification: Notification): NotificationEvent()
}