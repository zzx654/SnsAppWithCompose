package com.androiddev.data.repository.chat

import com.androiddev.data.remote.api.chat.ChatApi
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.repository.chat.ChatRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val api: ChatApi
): ChatRepository {
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