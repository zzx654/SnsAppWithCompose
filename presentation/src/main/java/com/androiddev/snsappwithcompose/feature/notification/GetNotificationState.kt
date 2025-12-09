package com.androiddev.snsappwithcompose.feature.notification
import com.androiddev.domain.model.NotificationItem

data class GetNotificationsState(
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val notifications: List<NotificationItem> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
)