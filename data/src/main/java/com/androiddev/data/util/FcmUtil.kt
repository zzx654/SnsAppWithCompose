package com.androiddev.data.util

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

suspend fun getFcmToken(): String {
    return runCatching {
        FirebaseMessaging.getInstance().token.await()
    }.getOrElse { exception ->
        Log.e("FcmToken", "FCM 토큰 발급 실패: ${exception.message}")
        ""
    }
}