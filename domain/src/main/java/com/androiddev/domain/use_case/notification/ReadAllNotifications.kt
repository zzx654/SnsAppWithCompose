package com.androiddev.domain.use_case.notification

import com.androiddev.domain.model.NotificationItem
import com.androiddev.domain.repository.notification.NotificationRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReadAllNotifications @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(): Flow<Resource<List<NotificationItem>>> = repository.readAllNotifications()

}