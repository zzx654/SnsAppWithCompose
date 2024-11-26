package com.androiddev.snsappwithcompose

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SnsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
       KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)

    }
}