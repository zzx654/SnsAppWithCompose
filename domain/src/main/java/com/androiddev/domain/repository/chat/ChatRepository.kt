package com.androiddev.domain.repository.chat

import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun createChatRequest(
        targetUserId: Long,
        postId: Long?,
        commentId: Long?
    ): Flow<Resource<Unit>>
}