package com.androiddev.domain.use_case.notification

import com.androiddev.domain.model.Notifications
import com.androiddev.domain.repository.notification.NotificationRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotifications @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notificationId:Long? = null,notificationDate:String? = null): Flow<Resource<Notifications>> = repository.getNotifications(notificationId,notificationDate)
}