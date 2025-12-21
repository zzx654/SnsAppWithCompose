package com.androiddev.domain.model

data class ReadNotificationResult (
    val unreadCount:Int?,
    val notificationActionResult: NotificationActionResult
)