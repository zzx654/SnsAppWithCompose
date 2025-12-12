package com.androiddev.data.repository.notification

import android.content.Context
import com.androiddev.data.remote.api.notification.NotificationApi
import com.androiddev.data.remote.dto.toNotifications
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.Notifications
import com.androiddev.domain.repository.notification.NotificationRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val context: Context,
    private val api:NotificationApi
):NotificationRepository {
    override suspend fun getNotifications(
        notificationId: Long?,
        notificationDate: String?
    ): Flow<Resource<Notifications>> = safeApiCall(
        context = context,
        apiCall = { api.getNotifications(notificationId,notificationDate) },
        mapToResource = { it.toNotifications(notifications = it.notifications, unreadCount = it.unreadCount) }
    )

    override suspend fun readAllNotifications(): Flow<Resource<Notifications>>
    = safeApiCall(
        context = context,
        apiCall = { api.readAllNotifications() },
        mapToResource = { it.toNotifications(notifications = it.notifications, unreadCount = it.unreadCount)}
    )

    override suspend fun deleteNotifications(): Flow<Resource<Notifications>>
    = safeApiCall(
        context = context,
        apiCall = { api.deleteNotifications() },
        mapToResource = { it.toNotifications(notifications = it.notifications, unreadCount = it.unreadCount)}
    )

}