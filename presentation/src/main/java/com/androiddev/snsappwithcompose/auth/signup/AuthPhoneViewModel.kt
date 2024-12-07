package com.androiddev.snsappwithcompose.auth.signup

import android.content.res.Resources
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.use_case.AuthPhoneUseCases
import com.androiddev.domain.use_case.InvalidPhoneNumberException
import com.androiddev.domain.use_case.SocialSignUpUseCase
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.Constants.AUTH_LIMITEDTIME
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.auth.util.UserPreferences
import com.androiddev.snsappwithcompose.util.Screen
import com.androiddev.snsappwithcompose.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthPhoneViewModel @Inject constructor(
    private val authPhoneUseCases: AuthPhoneUseCases,
    private val signUpUseCase: SocialSignUpUseCase,
    private val userPreferences: UserPreferences
) : AuthViewModel() {
    private val _phoneNumber = mutableStateOf("")
    val phoneNumber: State<String>
        get() = _phoneNumber

    fun onEvent(event:AuthPhoneEvent) {
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
                            when (result) {
                                is Resource.Success -> {
                                    _isLoading.value = false
                                    _isCodeReceived.value = true
                                    _limitTime.value = AUTH_LIMITEDTIME
                                    _authCodeField.value = authCodeField.value.copy(code = "",isError = false)
                                    timerStart()
                                }

                                is Resource.Error -> {
                                    _isLoading.value = false
                                    _eventFlow.emit(
                                        UiEvent.ShowToast(
                                            message = result.message ?: "에러가 발생했습니다"
                                        )
                                    )
                                }
                                is Resource.Loading -> {
                                    _isLoading.value = true
                                }
                            }
                        }
                    } catch (e: InvalidPhoneNumberException) {
                        _eventFlow.emit(
                            UiEvent.ShowToast(
                                message = e.message ?: "전화번호를 확인해주세요"
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
                            when (result) {
                                is Resource.Success -> {
                                    _isLoading.value = false
                                    result.data?.let { isCodeCorrect ->
                                        if(isCodeCorrect) {
                                            timerJob?.cancel()
                                            if(event.platform == Resources.getSystem().getString(R.string.email)) {
                                                _eventFlow.emit(UiEvent.navigate(Screen.SignUpScreen(phoneNumber.value)))
                                            } else {
                                                //sns가입시도
                                                socialSignUp(event.platform,event.account!!)
                                            }
                                        }
                                        else
                                            _authCodeField.value = authCodeField.value.copy(isError = true)
                                    }
                                }
                                is Resource.Error -> {
                                    _isLoading.value = false
                                    _eventFlow.emit(
                                        UiEvent.ShowToast(
                                            message = result.message ?: Resources.getSystem().getString(R.string.error)
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

    private fun socialSignUp(platform: String,account: String) {
        viewModelScope.launch {
            signUpUseCase(platform,account)
                .collect{ result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { userPreferences.saveAuthToken(it) }
                            //_eventFlow.emit(
                             //   UiEvent.navigate(
                              //      프로필작성화면
                               // )
                           // )
                        }
                        is Resource.Error -> {
                            _isLoading.value = false
                            _eventFlow.emit(
                                UiEvent.ShowToast(
                                    message = result.message ?: Resources.getSystem().getString(R.string.error)
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
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}