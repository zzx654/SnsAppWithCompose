package com.androiddev.domain.use_case.fcm

import android.util.Log
import com.androiddev.domain.repository.fcm.FcmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FcmTokenUseCase @Inject constructor(
    private val repository: FcmRepository
) {
    fun invoke(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.sendFcmToken(token)
            } catch (e: Exception) {
                Log.e("FCM", "토큰 전송 실패", e)
            }
        }
    }
}