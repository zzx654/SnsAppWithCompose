package com.androiddev.snsappwithcompose.auth.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.androiddev.snsappwithcompose.R
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

@Composable
fun KakaoSignInButton(
    onKaKaoSignInCompleted: (String) -> Unit,
    onError: (String) -> Unit,
) {
    val context = LocalContext.current
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e("KakaoLoginErr", "카카오계정으로 로그인 실패", error)
            error.message?.let { onError(it) }
        } else if (token != null) {
            UserApiClient.instance.me { user, error ->
                if (user != null){
                    Log.i("KaKaoLoginSuccess", "카카오계정으로 로그인 성공 ${user.id}")
                    onKaKaoSignInCompleted(user.id.toString())
                }
            }
        }
    }
    SocialMediaLogIn(
        icon = R.drawable.kakaotalk_logo,
        onClick = {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        Log.e("KakaoSignInErr", "카카오톡으로 로그인 실패", error)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                            // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                            return@loginWithKakaoTalk
                        }
                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    } else if (token != null) {
                        Log.i("KaKaoSignInSuccess", "카카오톡으로 로그인 성공 ${token.accessToken}")
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }
    )
}