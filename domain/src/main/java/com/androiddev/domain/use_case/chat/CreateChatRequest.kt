package com.androiddev.domain.use_case.chat

import com.androiddev.domain.repository.chat.ChatRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateChatRequest @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(
        targetUserId: Long,
        postId: Long? = null,
        commentId: Long? = null
    ): Flow<Resource<Unit>> = repository.createChatRequest(
        targetUserId = targetUserId,
        postId = postId,
        commentId = commentId
    )
}