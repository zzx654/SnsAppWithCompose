package com.androiddev.snsappwithcompose.feature.auth.signin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.SigninResult
import com.androiddev.domain.use_case.signin.SignInUseCases
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.state.AlertDialogStateV2
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.common.util.withFcmToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCases: SignInUseCases,
) : BaseViewModel() {
    private val _account = mutableStateOf("")
    val account: State<String>
        get() = _account
    private val _password = mutableStateOf("")
    val password: State<String>
        get() = _password
    private val _alertDialogState = MutableStateFlow(AlertDialogStateV2())
    val alertDialogState: StateFlow<AlertDialogStateV2> = _alertDialogState.asStateFlow()
    init {
        resetSignIn()
    }
    fun resetSignIn() {
        viewModelScope.launch {
            signInUseCases.resetSocialSignIn()
                .onFailure { exception ->
                    // Repository에서 던진 에러 메시지를 수신하여 UI 이벤트 발행
                    setEvent(
                        UiEvent.ShowToast(
                            UiText.DynamicString(exception.message ?: "소셜 로그아웃 실패")
                        )
                    )
                }
        }
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
                            result.handle(
                                onSuccess =  {
                                    handleSigninResult(event,it)
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
                            result.handle(
                                onSuccess = {
                                    handleSigninResult(event,it)
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
                    showSignInFailedAlert()
                }
            }
        }
    }
    private fun showSignInFailedAlert() {
        _alertDialogState.value = AlertDialogStateV2(
            title = UiText.StringResource(R.string.signin_failed),
            confirmText = UiText.StringResource(R.string.confirm),
            onClickConfirm = {
                resetDialogState()
            }
        )
    }
    protected fun resetDialogState() {
        _alertDialogState.value = AlertDialogStateV2()
    }
}