package com.androiddev.snsappwithcompose.auth.signin

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddev.data.util.UserPreferences
import com.androiddev.domain.model.SigninResponse
import com.androiddev.domain.use_case.SignInUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.AlertDialogState
import com.androiddev.snsappwithcompose.util.Screen
import com.androiddev.snsappwithcompose.util.UiEvent
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCases: SignInUseCases,
    private val userPreferences: UserPreferences,
    private val context: Context,
) : ViewModel() {
    private val _isLoading = mutableStateOf(false)
    val isLoading : State<Boolean>
        get() = _isLoading
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()
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
            is SignInEvent.emailSignIn -> {
                viewModelScope.launch {
                    signInUseCases.emailSignIn(account.value,password.value)
                        .collect { result ->
                            when(result) {
                                is Resource.Success -> {
                                    _isLoading.value = false
                                    result.data?.let {
                                        handleSigninResult(event,it)
                                    }
                                }
                                is Resource.Error -> {
                                    _isLoading.value = false
                                    _eventFlow.emit(
                                        UiEvent.ShowToast(
                                            message = result.message ?: getString(context,R.string.error)
                                        )
                                    )
                                }
                                is Resource.Loading -> {
                                    _isLoading.value = true
                                }
                            }
                        }
                }
            }
            is SignInEvent.socialSignIn -> {
                viewModelScope.launch {
                    signInUseCases.socialSignIn(event.platform,event.account)
                        .collect { result ->
                            when(result) {
                                is Resource.Success -> {
                                    _isLoading.value = false
                                    result.data?.let {
                                        handleSigninResult(event,it)
                                    }
                                }
                                is Resource.Error -> {
                                    _isLoading.value = false
                                    _eventFlow.emit(
                                        UiEvent.ShowToast(
                                            message = result.message ?: getString(context,R.string.error)
                                        )
                                    )
                                }
                                is Resource.Loading -> {
                                    _isLoading.value = true
                                }
                            }
                        }
                }
            }

            else -> null
        }
    }
    private fun handleSigninResult(event:SignInEvent,signinResult:SigninResponse) {
        viewModelScope.launch {
            if(signinResult.isMember) {
                //가입된 계정일때
                userPreferences.saveAuthToken(signinResult.token)
                if(signinResult.profileWritten) {
                    //홈화면으로 이동
                    _eventFlow.emit(
                        UiEvent.navigate(
                            screen = Screen.HomeScreen
                        )
                    )
                }
                else {
                    //프로필 작성화면으로이동
                    _eventFlow.emit(
                        UiEvent.navigate(
                            screen = Screen.CreateprofileScreen
                        )
                    )
                }
            } else {
                //가입안된 계정일때 핸드폰 인증화면으로 이동
                if(event is SignInEvent.socialSignIn) {
                    _eventFlow.emit(
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