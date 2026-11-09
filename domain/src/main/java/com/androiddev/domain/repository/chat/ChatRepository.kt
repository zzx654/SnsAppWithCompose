package com.androiddev.domain.repository.chat

import androidx.paging.PagingData
import com.androiddev.domain.model.ChatRequest
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatRequests(): Flow<PagingData<ChatRequest>>
    suspend fun createChatRequest(
        targetUserId: Long,
        postId: Long?,
        commentId: Long?
    ): Flow<Resource<Unit>>
}