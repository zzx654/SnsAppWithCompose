package com.androiddev.snsappwithcompose

import android.app.Application
import com.androiddev.domain.use_case.fcm.FcmTokenUseCase
import com.androiddev.snsappwithcompose.common.util.withFcmToken

import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NidOAuth
import dagger.hilt.android.HiltAndroidApp

import javax.inject.Inject


@HiltAndroidApp
class SnsApplication : Application() {
    @Inject
    lateinit var fcmTokenUseCase: FcmTokenUseCase
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)
        NidOAuth.initialize(this,BuildConfig.NAVER_CLIENT_ID,BuildConfig.NAVER_CLIENT_SECRET,getString(R.string.app_name))
        withFcmToken { token ->
            fcmTokenUseCase.invoke(token)
        }
    }
}