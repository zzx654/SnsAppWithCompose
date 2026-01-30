package com.androiddev.snsappwithcompose.feature.notification

data class PendingNotification(
    val notificationId:Long,
    val type: String,
    val extraJson: String
)