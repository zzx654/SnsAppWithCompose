package com.androiddev.domain.model

data class NotificationExtra(
    val postId: Long? = null,
    val commentId: Long? = null,
    val followerId: Long? = null
)

data class NotificationItem(
    val id: Long,
    val type: String,
    val content: String,
    val extrajson: NotificationExtra,
    val isRead: Boolean,
    val date: String,
    val elapsedTime: String
)