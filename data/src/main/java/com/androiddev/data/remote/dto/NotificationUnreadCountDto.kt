package com.androiddev.data.remote.dto

data class UnreadNotificationCountDto(
    val unReadCount:Int
)
fun UnreadNotificationCountDto.toDomain():Int {
    return unReadCount
}