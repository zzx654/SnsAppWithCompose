package com.androiddev.data.repository.signin

import android.content.Context
import android.util.Log
import com.androiddev.data.local.UserPreferences
import com.androiddev.data.remote.api.signin.SignInApi
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.data.remote.dto.toSigninWithTokenResult
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.SigninResult
import com.androiddev.domain.model.SigninWithTokenResult
import com.androiddev.domain.repository.signin.SigninRepository
import com.androiddev.domain.util.Resource
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.util.NidOAuthCallback
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SigninRepositoryImpl @Inject constructor(
    private val api: SignInApi,
    private val context: Context,
    private val userPreferences: UserPreferences
) : SigninRepository {
    override suspend fun resetSocialSignIn(): Result<Unit> {
        return runCatching {
            // 1. 카카오 로그아웃 수행
            logoutKakao()
            // 2. 네이버 로그아웃 수행
            logoutNaver()
        }
    }
    // Kakao 콜백을 Coroutine Suspend로 변환
    private suspend fun logoutKakao(): Unit = suspendCancellableCoroutine { continuation ->
        UserApiClient.instance.me { user, error ->
            if (user != null) {
                UserApiClient.instance.logout { logoutError ->
                    if (logoutError != null) {
                        Log.e("KakaoErr", logoutError.message ?: "Kakao logout error")
                    }
                    // 카카오는 에러가 나더라도 다음 로직(네이버) 진행을 위해 resume
                    if (continuation.isActive) continuation.resume(Unit)
                }
            } else {
                if (continuation.isActive) continuation.resume(Unit)
            }
        }
    }

    // Naver 콜백을 Coroutine Suspend로 변환
    private suspend fun logoutNaver(): Unit = suspendCancellableCoroutine { continuation ->
        NidOAuth.logout(object : NidOAuthCallback {
            override fun onSuccess() {
                Log.i("Naver", "naverLogoutSuccess")
                if (continuation.isActive) continuation.resume(Unit)
            }

            override fun onFailure(errorCode: String, errorDesc: String) {
                Log.e("Naver", "errorCode: $errorCode, errorDesc: $errorDesc")
                if (continuation.isActive) {
                    continuation.resumeWithException(
                        Exception("errorCode:$errorCode, errorDesc:$errorDesc")
                    )
                }
            }
        })
    }



    override suspend fun socialSignIn(
        platform: String,
        account: String,
        fcmToken: String
    ): Flow<Resource<SigninResult>> = safeApiCall(
        context = context,
        apiCall = { api.socialSignIn(platform,account,fcmToken) },
        mapToResource = {
            it.toDomain()
        },
        onSuccess = {
            if(it.isMember&&it.token.isNullOrEmpty())
                userPreferences.saveAuthToken(it.token)
        }
    )
    override suspend fun emailSignIn(
        account: String,
        password: String,
        fcmToken: String,
    ): Flow<Resource<SigninResult>> = safeApiCall(
        context = context,
        apiCall = { api.emailSignIn(account,password,fcmToken) },
        mapToResource = { it.toDomain()},
        onSuccess = {
            if(it.isMember&&it.token.isNullOrEmpty())
                userPreferences.saveAuthToken(it.token)
        }
    )

    override suspend fun signInWithToken(): Flow<Resource<SigninWithTokenResult>> =
        safeApiCall(
            context = context,
            apiCall = { api.signInWithToken() },
            mapToResource = {
                it.toSigninWithTokenResult()
            }
        )
}
