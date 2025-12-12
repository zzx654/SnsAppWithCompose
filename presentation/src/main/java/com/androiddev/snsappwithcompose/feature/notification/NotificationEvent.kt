package com.androiddev.snsappwithcompose.feature.notification

sealed class NotificationEvent {
    data object LoadNextNotifications: NotificationEvent()
    data object RefreshNotifictions: NotificationEvent()
    data object DeleteNotifications: NotificationEvent()
    data object ReadAllNotifications: NotificationEvent()
}