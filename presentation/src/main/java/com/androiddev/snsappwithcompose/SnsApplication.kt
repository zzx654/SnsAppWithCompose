package com.androiddev.snsappwithcompose

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NidOAuth
import dagger.hilt.android.HiltAndroidApp

const val URGENT = "urgent"
const val URGENT_NAME = "urgent_name"
@HiltAndroidApp
class SnsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)
        NidOAuth.initialize(this,BuildConfig.NAVER_CLIENT_ID,BuildConfig.NAVER_CLIENT_SECRET,getString(R.string.app_name))
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "FCM 토큰을 얻는 데 실패했습니다.", task.exception)
                return@addOnCompleteListener
            }

            // FCM 토큰을 얻은 후
            val token = task.result
            println("FCM 토큰: $token")



        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

       //     val urgentChannel =
          //      NotificationChannel(URGENT, URGENT_NAME, NotificationManager.IMPORTANCE_HIGH)

       //     val notificationManager =
       //         getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
       //     notificationManager.createNotificationChannel(urgentChannel)
        }
    }
}