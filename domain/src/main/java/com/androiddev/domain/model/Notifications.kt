package com.androiddev.domain.model

data class Notifications(
    val notifications:List<NotificationItem>,
    val unreadCount:Int
)