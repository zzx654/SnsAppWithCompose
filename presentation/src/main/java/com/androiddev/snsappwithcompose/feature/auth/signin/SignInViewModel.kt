package com.androiddev.snsappwithcompose.feature.auth.signin

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.data.local.UserPreferences
import com.androiddev.domain.model.SigninResult
import com.androiddev.domain.use_case.signin.SignInUseCases
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.state.AlertDialogState
import com.androiddev.snsappwithcompose.common.base.viewmodel.BaseViewModel
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.common.util.withFcmToken
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.util.NidOAuthCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCases: SignInUseCases,
    private val userPreferences: UserPreferences,
    @ApplicationContext context: Context,
) : BaseViewModel(context) {
    private val _account = mutableStateOf("")
    val account: State<String>
        get() = _account
    private val _password = mutableStateOf("")
    val password: State<String>
        get() = _password
    private val _alertDialogState: MutableState<AlertDialogState> = mutableStateOf(AlertDialogState())
    val alertDialogState: State<AlertDialogState>
        get() = _alertDialogState
    init {
        resetSignIn()
    }
    fun resetSignIn() {
        UserApiClient.instance.me { user, error ->
            if(user!=null) {
                UserApiClient.instance.logout { error ->
                    if(error !=null)
                        Log.e("kakaoErr", error.message?: "kakaologout error")
                    else
                        naverLogout()
                }
            } else naverLogout()
        }
    }
    fun naverLogout() {
        NidOAuth.logout( object : NidOAuthCallback {
            override fun onSuccess() {
                //클라이언트에서 토큰 삭제를 성공한 상태
                Log.i("naver","naverLogoutSuccess")
            }
            override fun onFailure(
                errorCode: String,
                errorDesc: String,
            ) {
                viewModelScope.launch {
                    setEvent(
                        UiEvent.ShowToast(
                            UiText.DynamicString("errorCode:$errorCode, errorDesc:$errorDesc"))
                    )
                }
            }
        })
    }
    fun onEvent(event: SignInEvent) {
        when(event) {
            is SignInEvent.TypeAccount -> {
                _account.value = event.account
            }
            is SignInEvent.TypePwd -> {
                _password.value = event.password
            }
            is SignInEvent.EmailSignIn -> {
                withFcmToken { token ->
                    viewModelScope.launch {
                        signInUseCases.emailSignIn(
                            account = account.value,
                            password = password.value,
                            fcmToken = token
                        ).collect { result ->
                            handleResource(
                                resource = result,
                                onSuccess = { data ->
                                    handleSigninResult(event,data)
                                }
                            )
                        }
                    }
                }
            }
            is SignInEvent.SocialSignIn -> {

                withFcmToken { token ->
                    viewModelScope.launch {
                        signInUseCases.socialSignIn(
                            platform = event.platform,
                            account = event.account,
                            fcmToken = token
                        ).collect { result ->
                            handleResource(
                                resource = result,
                                onSuccess = { data ->
                                    handleSigninResult(event,data)
                                }
                            )
                        }
                    }
                }
            }
            else -> null
        }
    }
    private fun handleSigninResult(event:SignInEvent,signinResult: SigninResult) {
        viewModelScope.launch {
            if(signinResult.isMember) {
                //가입된 계정일때
                userPreferences.saveAuthToken(signinResult.token)
                if(signinResult.profileWritten) {
                    //홈화면으로 이동
                    setEvent(
                        UiEvent.navigate(
                            screen = Screen.MainScreen,
                            userId = signinResult.userId
                        )
                    )
                }
                else {
                    //프로필 작성화면으로이동
                    setEvent(
                        UiEvent.navigate(
                            screen = Screen.CreateprofileScreen,
                            userId = signinResult.userId
                        )
                    )
                }
            } else {
                //가입안된 계정일때 핸드폰 인증화면으로 이동
                if(event is SignInEvent.SocialSignIn) {
                    setEvent(
                        UiEvent.navigate(
                            screen = Screen.AuthPhoneScreen(event.platform,event.account)
                        )
                    )
                }
                else {
                    showSignInFialedAlert()
                }
            }
        }
    }
    private fun showSignInFialedAlert() {
        _alertDialogState.value = AlertDialogState(
            title = getString(context,R.string.signin_failed),
            confirmText = getString(context,R.string.confirm),
            onClickConfirm = {
                resetDialogState()
            }
        )
    }
    protected fun resetDialogState() {
        _alertDialogState.value = AlertDialogState()
    }
}