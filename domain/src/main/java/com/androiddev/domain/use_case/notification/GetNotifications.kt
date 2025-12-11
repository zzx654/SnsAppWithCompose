package com.androiddev.domain.use_case.notification

import android.app.Notification
import com.androiddev.domain.model.NotificationItem
import com.androiddev.domain.model.Posts
import com.androiddev.domain.repository.notification.NotificationRepository
import com.androiddev.domain.repository.postlist.GetPostsRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotifications @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notificationId:Int? = null,notificationDate:String? = null): Flow<Resource<List<NotificationItem>>> = repository.getNotifications(notificationId,notificationDate)
}