package com.androiddev.snsappwithcompose

import android.app.Application
import com.androiddev.domain.use_case.fcm.FcmTokenUseCase
import com.androiddev.data.util.getFcmToken
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NidOAuth
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

import javax.inject.Inject


@HiltAndroidApp
class SnsApplication : Application() {
    @Inject
    lateinit var fcmTokenUseCase: FcmTokenUseCase

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)
        NidOAuth.initialize(this,BuildConfig.NAVER_CLIENT_ID,BuildConfig.NAVER_CLIENT_SECRET,getString(R.string.app_name))
        applicationScope.launch {
            val token = getFcmToken()
            if (token.isNotEmpty()) {
                fcmTokenUseCase.invoke(token)
            }
        }
    }
}