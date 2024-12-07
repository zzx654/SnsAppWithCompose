package com.androiddev.snsappwithcompose.auth.signup

import android.content.res.Resources
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.use_case.EmailSignUpUseCases
import com.androiddev.domain.use_case.InvalidEmailException
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.Constants
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailSignUpViewModel @Inject constructor(
    private val emailSignUpUseCases: EmailSignUpUseCases
) : AuthViewModel() {


    private val _email = mutableStateOf("")
    val email: State<String>
        get() = _email

    private val _password = mutableStateOf("")
    val password: State<String>
        get() = _password

    private val _repeatPw = mutableStateOf("")
    val repeatPw: State<String>
        get() = _repeatPw
    fun onEvent(event: EmailSignUpEvent) {
        when(event) {
            is EmailSignUpEvent.TypeEmail -> {
                _email.value = event.email
                if (isCodeReceived.value) {
                    //코드를 이미 받은상태이면
                    _isCodeReceived.value = false//이값에 따라 인증번호 칸이 사라지고 인증하기 버튼 비활성화
                    timerJob?.cancel()
                }
            }
            is EmailSignUpEvent.RequestAuthCode -> {
                viewModelScope.launch {
                    try {
                        emailSignUpUseCases.requestAuthCode(email.value).collect { result ->
                            when (result) {
                                is Resource.Success -> {
                                    _isLoading.value = false
                                    _isCodeReceived.value = true
                                    _limitTime.value = Constants.AUTH_LIMITEDTIME
                                    _authCodeField.value = authCodeField.value.copy(code = "",isError = false)
                                    timerStart()
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
                    } catch (e: InvalidEmailException) {
                        _eventFlow.emit(
                            UiEvent.ShowToast(
                                message = e.message ?: Resources.getSystem().getString(R.string.check_email)
                            )
                        )
                    }
                }
            }
            is EmailSignUpEvent.TypeAuthCode -> {
                _authCodeField.value = authCodeField.value.copy(code = event.authCode)
            }
            is EmailSignUpEvent.TypePwd -> {
                _password.value = event.password
            }
            is EmailSignUpEvent.TypeRepeatPwd -> {
                _repeatPw.value = event.repeatPwd
            }
        }


    }


}