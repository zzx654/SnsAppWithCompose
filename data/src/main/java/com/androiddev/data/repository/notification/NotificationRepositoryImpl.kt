package com.androiddev.data.repository.notification

import android.content.Context
import androidx.paging.PagingData
import com.androiddev.data.notification.NotificationHelper
import com.androiddev.data.paging.createPager
import com.androiddev.data.paging.pagingsource.GenericPagingSource
import com.androiddev.data.paging.pagingstrategy.NotificationStrategy
import com.androiddev.data.remote.api.notification.NotificationApi
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.data.remote.dto.toReadNotificationResult
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.Notification
import com.androiddev.domain.model.ReadNotificationResult
import com.androiddev.domain.repository.notification.NotificationRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val context: Context,
    private val api:NotificationApi,
    private val notificationHelper: NotificationHelper
):NotificationRepository {
    override fun getNotifications(
    ): Flow<PagingData<Notification>> = createPager {
        GenericPagingSource(
            NotificationStrategy(
                api = api
            )
        )
    }

    override suspend fun getUnreadNotificationCount(): Flow<Resource<Int>>
    = safeApiCall(
        apiCall = { api.getUnreadNotificationCount() },
        mapToResource = { it.toDomain() }
    )


    override suspend fun readAllNotifications(): Flow<Resource<Unit>>
    = safeApiCall(
        apiCall = { api.readAllNotifications() },
        mapToResource = {
            notificationHelper.cancelAllNotifications()
        }
    )

    override suspend fun deleteNotifications(): Flow<Resource<Unit>>
    = safeApiCall(
        apiCall = { api.deleteNotifications() },
        mapToResource = {
            notificationHelper.cancelAllNotifications()
        }
    )

    override suspend fun readNotification(notificationId:Long): Flow<Resource<ReadNotificationResult>> = safeApiCall(
        context = context,
        apiCall = { api.readNotification(notificationId) },
        mapToResource = {
            notificationHelper.cancelNotification(notificationId)
            it.toReadNotificationResult(
            )
        }
    )

}