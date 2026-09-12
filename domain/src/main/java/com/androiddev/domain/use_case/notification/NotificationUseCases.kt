package com.androiddev.domain.use_case.notification

import javax.inject.Inject

data class NotificationUseCases @Inject constructor(
    val getNotifications: GetNotifications,
    val getUnreadNotificationCount: GetUnreadNotificationCount,
    val readAllNotifications: ReadAllNotifications,
    val deleteNotifications: DeleteNotifications,
    val readNotification: ReadNotification
)