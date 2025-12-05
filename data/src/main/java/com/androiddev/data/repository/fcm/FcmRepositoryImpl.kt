package com.androiddev.data.repository.fcm

import com.androiddev.data.local.UserPreferences
import com.androiddev.data.remote.api.fcm.FcmApi
import com.androiddev.domain.repository.fcm.FcmRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class FcmRepositoryImpl @Inject constructor(
    private val api: FcmApi,
    private val userPreferences: UserPreferences
) : FcmRepository {
    override suspend fun sendFcmToken(token: String) {
        val authToken:String?
        runBlocking { authToken = userPreferences.authToken.firstOrNull() }
        if (authToken.isNullOrEmpty()) return  // 회원가입 전이면 전송 안함
        api.sendFcmToken(token)
    }

}