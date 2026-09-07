package com.androiddev.domain.repository.notification

import androidx.paging.PagingData
import com.androiddev.domain.model.Notification
import com.androiddev.domain.model.Notifications
import com.androiddev.domain.model.ReadNotificationResult
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun getNotifications(onUnreadCountUpdated:(Int)->Unit): Flow<PagingData<Notification>>
    suspend fun readAllNotifications(): Flow<Resource<Unit>>
    suspend fun deleteNotifications(): Flow<Resource<Unit>>
    suspend fun readNotification(notificationId: Long): Flow<Resource<ReadNotificationResult>>
}