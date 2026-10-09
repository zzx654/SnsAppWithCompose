package com.androiddev.snsappwithcompose.feature.auth.signup.authphone

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.androiddev.data.local.UserPreferences
import com.androiddev.domain.use_case.signup.authphone.AuthPhoneUseCases
import com.androiddev.domain.use_case.signup.authphone.InvalidPhoneNumberException
import com.androiddev.domain.use_case.signup.socialsignup.SocialSignUpUseCase
import com.androiddev.snsappwithcompose.common.util.Constants.AUTH_LIMITEDTIME
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.state.AlertDialogState
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.state.AlertDialogStateV2
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.common.util.withFcmToken
import com.androiddev.snsappwithcompose.feature.auth.signup.AuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthPhoneViewModel @Inject constructor(
    private val authPhoneUseCases: AuthPhoneUseCases,
    private val signUpUseCase: SocialSignUpUseCase,
    private val userPreferences: UserPreferences,
    savedStateHandle: SavedStateHandle
) : AuthViewModel() {
    private val args = savedStateHandle.toRoute<Screen.AuthPhoneScreen>()

    //  String -> Platform Enum으로 관리하여 타입 안정성 확보
    private val platform: Platform = Platform.fromKey(args.platform)
    private val account: String = args.account.orEmpty()

    private val _phoneNumber = mutableStateOf("")
    val phoneNumber: State<String>
        get() = _phoneNumber


    fun onEvent(event: AuthPhoneEvent) {
        when (event) {
            is AuthPhoneEvent.TypePhoneNumber -> {
                _phoneNumber.value = event.phoneNumber
                if (_isCodeReceived.value) {
                    //코드를 이미 받은상태이면
                    _isCodeReceived.value = false//이값에 따라 인증번호 칸이 사라지고 인증하기 버튼 비활성화
                    timerJob?.cancel()
                }
            }
            is AuthPhoneEvent.RequestAuthCode -> {
                viewModelScope.launch {

                    try {
                        authPhoneUseCases.requestAuthCode(phoneNumber.value).collect { result ->
                            result.handle (
                                onSuccess = { data ->
                                    if(data.isValid) {

                                        _isCodeReceived.value = true
                                        _limitTime.value = AUTH_LIMITEDTIME
                                        _authCodeField.value = authCodeField.value.copy(code = "",isError = false)
                                        timerStart()
                                    } else {
                                        showPhoneExistAlert()
                                    }
                                }
                            )
                        }
                    } catch (e: InvalidPhoneNumberException) {
                        setEvent(
                            UiEvent.ShowToast(
                                message = e.message?.let{ UiText.DynamicString(it)} ?: UiText.StringResource(R.string.check_phonenumber)
                            )
                        )
                    }
                }
            }
            is AuthPhoneEvent.TypeAuthCode -> {
                _authCodeField.value = authCodeField.value.copy(code = event.authCode)
            }
            is AuthPhoneEvent.AuthenticateCode -> {
                viewModelScope.launch {
                    authPhoneUseCases.authenticateCode(phoneNumber.value, authCodeField.value.code)
                        .collect { result ->
                            result.handle(
                                onSuccess = { data ->
                                    if(data.isCorrect) {
                                        timerJob?.cancel()
                                        if(!platform.isSocial) {
                                            setEvent(
                                                UiEvent.navigate(Screen.SignUpScreen(phoneNumber.value))
                                            )
                                        } else {
                                            //sns가입시도
                                            socialSignUp(
                                                platform = args.platform,
                                                account = account?:"",
                                                phoneNumber = phoneNumber.value,
                                            )


                                        }
                                    }
                                    else
                                        _authCodeField.value = authCodeField.value.copy(isError = true)
                                }
                            )
                        }
                }
            }
        }
    }
    private fun socialSignUp(platform: String,account: String,phoneNumber: String) {
        viewModelScope.launch {
            signUpUseCase(platform,account,phoneNumber)
                .collect{ result ->
                    result.handle(
                        onSuccess = { data ->
                            setEvent(
                                UiEvent.navigate(
                                    Screen.CreateprofileScreen
                                )
                            )
                        }
                    )
                }
        }
    }
    private fun showPhoneExistAlert() {
        _alertDialogState.value = AlertDialogStateV2(
            title = UiText.StringResource(R.string.phonenumber_exist),
            confirmText = UiText.StringResource(R.string.confirm),
            onClickConfirm = {
                resetDialogState()
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
enum class Platform(val apiKey: String) {
    EMAIL("email"),
    NAVER("naver"),
    KAKAO("kakao");

    val isSocial: Boolean
        get() = this != EMAIL

    companion object {
        fun fromKey(key: String): Platform {
            return entries.find { it.apiKey.lowercase() == key.lowercase() } ?: EMAIL
        }
    }
}