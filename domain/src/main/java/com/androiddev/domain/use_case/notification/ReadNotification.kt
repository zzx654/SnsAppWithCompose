package com.androiddev.domain.use_case.notification

import com.androiddev.domain.model.ReadNotificationResult
import com.androiddev.domain.repository.notification.NotificationRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReadNotification @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notificationId:Long): Flow<Resource<ReadNotificationResult>> = repository.readNotification(notificationId)

}