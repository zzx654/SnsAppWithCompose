package com.androiddev.domain.use_case.chat

import androidx.paging.PagingData
import com.androiddev.domain.model.ChatRequest
import com.androiddev.domain.repository.chat.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatRequests @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(): Flow<PagingData<ChatRequest>>
            = repository.getChatRequests()
}