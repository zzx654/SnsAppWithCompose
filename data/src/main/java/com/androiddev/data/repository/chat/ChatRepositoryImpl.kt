package com.androiddev.data.repository.chat

import androidx.paging.PagingData
import com.androiddev.data.paging.createPager
import com.androiddev.data.paging.pagingsource.GenericPagingSource
import com.androiddev.data.paging.pagingstrategy.ChatRequestStrategy
import com.androiddev.data.remote.api.chat.ChatApi
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.ChatRequest
import com.androiddev.domain.repository.chat.ChatRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val api: ChatApi
): ChatRepository {
    override fun getChatRequests(): Flow<PagingData<ChatRequest>> = createPager {
        GenericPagingSource(
            ChatRequestStrategy(api = api)
        )
    }
    override suspend fun createChatRequest(
        targetUserId: Long,
        postId: Long?,
        commentId: Long?
    ): Flow<Resource<Unit>> = safeApiCall(
        apiCall = {
            api.createChatRequest(
                targetuserid = targetUserId,
                postid = postId,
                commentid = commentId
            )
        },
        mapToResource = {
        }
    )
}