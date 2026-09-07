package com.androiddev.data.paging.pagingstrategy

import com.androiddev.data.paging.cursor.NotificationCursor
import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.api.notification.NotificationApi
import com.androiddev.data.remote.dto.NotificationsDto
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.domain.model.Notification
import retrofit2.Response


class NotificationStrategy (
    private val api: NotificationApi,
    private val onUnreadCountUpdated: (Int) -> Unit
): PagingStrategy<NotificationsDto, Notification, NotificationCursor> {
    override suspend fun fetch(cursor: NotificationCursor?): Response<BaseApiResponse<NotificationsDto>> {
        return api.getNotifications(
            notificationid = cursor?.notificationId,
            notificationdate = cursor?.notificationDate
        )
    }

    override fun mapToDomain(data: NotificationsDto): List<Notification> {
        val result = data.toDomain()
        onUnreadCountUpdated(result.unreadCount)

        return result.notifications
    }

    override fun extractNextCursor(items: List<Notification>, pageSize: Int): NotificationCursor? {
        if (items.isEmpty() || items.size < pageSize) return null
        val lastItem = items.last()
        return NotificationCursor(notificationId = lastItem.id, notificationDate = lastItem.date)
    }


}