package com.androiddev.data.paging.pagingstrategy

import com.androiddev.data.paging.cursor.ChatRequestCursor
import com.androiddev.data.paging.cursor.NotificationCursor
import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.api.chat.ChatApi
import com.androiddev.data.remote.api.notification.NotificationApi
import com.androiddev.data.remote.dto.ChatRequestsDto
import com.androiddev.data.remote.dto.NotificationsDto
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.domain.model.ChatRequest
import com.androiddev.domain.model.Notification
import retrofit2.Response


class ChatRequestStrategy (
    private val api: ChatApi,
): PagingStrategy<ChatRequestsDto, ChatRequest, ChatRequestCursor> {
    override suspend fun fetch(cursor: ChatRequestCursor?): Response<BaseApiResponse<ChatRequestsDto>> {
        return api.getChatRequests(
            chatrequestid = cursor?.chatRequestId,
            chatrequestdate = cursor?.chatRequestDate
        )
    }

    override fun mapToDomain(data: ChatRequestsDto): List<ChatRequest> {

        return data.toDomain()
    }

    override fun extractNextCursor(items: List<ChatRequest>, pageSize: Int): ChatRequestCursor? {
        if (items.isEmpty() || items.size < pageSize) return null
        val lastItem = items.last()
        return ChatRequestCursor(chatRequestId = lastItem.id, chatRequestDate = lastItem.createdAt)
    }


}