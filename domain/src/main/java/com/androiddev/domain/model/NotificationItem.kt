package com.androiddev.domain.model

data class NotificationExtra(
    val postId: Int? = null,
    val commentId: Int? = null,
    val followerId: Int? = null
)

data class NotificationItem(
    val id: Long,
    val type: String,
    val content: String,
    val extrajson: NotificationExtra,
    val isRead: Boolean,
    val date: String
)