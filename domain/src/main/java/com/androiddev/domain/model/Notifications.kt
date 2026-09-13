package com.androiddev.domain.model

data class Notifications(
    val notifications:List<Notification>,
    val unreadCount:Int
)