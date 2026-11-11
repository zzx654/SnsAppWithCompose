package com.androiddev.data.remote.dto

data class ChatRequestsCountDto(
    val chatRequestsCount:Int
)
fun ChatRequestsCountDto.toDomain():Int {
    return chatRequestsCount
}