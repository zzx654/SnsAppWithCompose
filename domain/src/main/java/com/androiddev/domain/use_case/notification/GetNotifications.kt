package com.androiddev.domain.use_case.notification

import androidx.paging.PagingData
import com.androiddev.domain.model.Notification
import com.androiddev.domain.repository.notification.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotifications @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(onUnreadCountUpdated:(Int) -> Unit): Flow<PagingData<Notification>>
    = repository.getNotifications(onUnreadCountUpdated)
}