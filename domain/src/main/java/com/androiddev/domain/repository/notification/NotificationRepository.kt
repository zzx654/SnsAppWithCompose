package com.androiddev.domain.repository.notification

import com.androiddev.domain.model.NotificationItem
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun getNotifications(notificationId:Long?,notificationDate:String?): Flow<Resource<List<NotificationItem>>>
}