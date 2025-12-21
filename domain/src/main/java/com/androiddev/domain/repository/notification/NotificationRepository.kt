package com.androiddev.domain.repository.notification

import com.androiddev.domain.model.Notifications
import com.androiddev.domain.model.ReadNotificationResult
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun getNotifications(notificationId:Long?,notificationDate:String?): Flow<Resource<Notifications>>
    suspend fun readAllNotifications(): Flow<Resource<Notifications>>
    suspend fun deleteNotifications(): Flow<Resource<Notifications>>
    suspend fun readNotification(notificationId: Long): Flow<Resource<ReadNotificationResult>>
}