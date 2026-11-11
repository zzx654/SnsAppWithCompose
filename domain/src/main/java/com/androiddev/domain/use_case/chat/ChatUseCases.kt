package com.androiddev.domain.use_case.chat

import javax.inject.Inject

data class ChatUseCases @Inject constructor(
    val createChatRequest: CreateChatRequest,
    val getChatRequests: GetChatRequests,
    val getChatRequestsCount: GetChatRequestsCount
)