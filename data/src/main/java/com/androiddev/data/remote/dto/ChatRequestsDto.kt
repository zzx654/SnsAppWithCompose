package com.androiddev.data.remote.dto

data class ChatRequestsDto (
    val chatRequests: List<ChatRequestDto>
)

fun ChatRequestsDto.toDomain()  = chatRequests.map { it.toDomain() }