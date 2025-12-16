package com.androiddev.snsappwithcompose.feature.notification

import com.androiddev.domain.model.NotificationItem

sealed class NotificationEvent {
    data object LoadNextNotifications: NotificationEvent()
    data object RefreshNotifictions: NotificationEvent()
    data object DeleteNotifications: NotificationEvent()
    data object ReadAllNotifications: NotificationEvent()
    data class ReadNotification(val notification: NotificationItem): NotificationEvent()
}