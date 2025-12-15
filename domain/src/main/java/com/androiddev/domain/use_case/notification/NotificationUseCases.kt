package com.androiddev.domain.use_case.notification

data class NotificationUseCases(
    val getNotifications: GetNotifications,
    val readAllNotifications: ReadAllNotifications,
    val deleteNotifications: DeleteNotifications,
    val readNotification: ReadNotification
)