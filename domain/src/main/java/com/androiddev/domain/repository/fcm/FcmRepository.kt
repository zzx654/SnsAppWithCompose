package com.androiddev.domain.repository.fcm


interface FcmRepository {
    suspend fun sendFcmToken(token: String)
}