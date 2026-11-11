package com.androiddev.domain.use_case.chat

import com.androiddev.domain.repository.chat.ChatRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatRequestsCount @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(): Flow<Resource<Int>>
            = repository.getChatRequestsCount()
}