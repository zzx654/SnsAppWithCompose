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
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.state.AlertDialogState
import com.androiddev.snsappwithcompose.common.base.viewmodel.BaseViewModel
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.state.UiEvent
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
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
        NidOAuthLogin().callDeleteTokenApi(object : OAuthLoginCallback {
            override fun onSuccess() {
                // 서버에서 토큰 삭제에 성공한 상태
                Log.i("naver","naverLogoutSuccess")

            }
            override fun onFailure(httpStatus: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없음
                Log.e("naverErr", "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                Log.e("naverErr", "errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}")
            }
            override fun onError(errorCode: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없음
                onFailure(errorCode, message)
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
                viewModelScope.launch {
                    signInUseCases.emailSignIn(account.value,password.value)
                        .collect { result ->
                            when(result) {
                                is Resource.Success -> {
                                    setLoading(false)
                                    result.data?.let {
                                        handleSigninResult(event,it)
                                    }
                                }
                                is Resource.Error -> {
                                    setLoading(false)
                                    setEvent(
                                        UiEvent.ShowToast(
                                            message = result.message ?: getString(context,R.string.error)
                                        )
                                    )
                                }
                                is Resource.Loading -> {
                                    setLoading(true)
                                }

                                is Resource.TokenExpired -> null
                            }
                        }
                }
            }
            is SignInEvent.SocialSignIn -> {
                viewModelScope.launch {
                    signInUseCases.socialSignIn(event.platform,event.account)
                        .collect { result ->
                            when(result) {
                                is Resource.Success -> {
                                    setLoading(false)
                                    result.data?.let {
                                        handleSigninResult(event,it)
                                    }
                                }
                                is Resource.Error -> {
                                    setLoading(false)
                                    setEvent(
                                        UiEvent.ShowToast(
                                            message = result.message ?: getString(context,R.string.error)
                                        )
                                    )
                                }
                                is Resource.Loading -> {
                                    setLoading(true)
                                }

                                is Resource.TokenExpired -> null
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