package com.androiddev.snsappwithcompose.auth.signup

import android.content.Context
import android.content.res.Resources
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.use_case.EmailSignUpUseCases
import com.androiddev.domain.use_case.InvalidEmailException
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.Constants
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.AlertDialogState
import com.androiddev.snsappwithcompose.util.Screen
import com.androiddev.snsappwithcompose.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailSignUpViewModel @Inject constructor(
    private val emailSignUpUseCases: EmailSignUpUseCases,
    private val context: Context
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
                                    result.data?.let { emailExist ->
                                        if(emailExist) {
                                            //다이얼로그 추가
                                            showEmailExistAlert()
                                        } else {
                                            _isCodeReceived.value = true
                                            _limitTime.value = Constants.AUTH_LIMITEDTIME
                                            _authCodeField.value = authCodeField.value.copy(code = "",isError = false)
                                            timerStart()
                                        }
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
                    } catch (e: InvalidEmailException) {
                        _eventFlow.emit(
                            UiEvent.ShowToast(
                                message = e.message ?: getString(context,R.string.check_email)
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
            is EmailSignUpEvent.EmailSignUp -> {
                viewModelScope.launch {
                    emailSignUpUseCases.emailSignUp(email.value,password.value,event.phonenumber,authCodeField.value.code)
                        .collect { result ->
                            when(result) {
                                is Resource.Success -> {
                                    _isLoading.value = false
                                    result.data?.let { isCodeCorrect ->
                                        if(isCodeCorrect) {
                                            // 확인누르면 로그인화면으로 가는 다이얼로그추가
                                            showSignUpCompletedAlert()
                                        }
                                        else {
                                            //인증번호가 틀렸다는 다이얼로그 추가
                                            showWrongCodeAlert()
                                        }

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
        }
    }
    private fun showEmailExistAlert() {
        _alertDialogState.value = AlertDialogState(
            title = getString(context,R.string.email_exist),
            confirmText = getString(context,R.string.confirm),
            onClickConfirm = {
                resetDialogState()
            }
        )
    }
    private fun showWrongCodeAlert() {
        _alertDialogState.value = AlertDialogState(
            title = getString(context,R.string.wrong_code),
            confirmText = getString(context,R.string.confirm),
            onClickConfirm = {
                resetDialogState()
            }
        )
    }
    private fun showSignUpCompletedAlert() {
        _alertDialogState.value = AlertDialogState(
            title = getString(context,R.string.signup_completed),
            confirmText = getString(context,R.string.confirm),
            onClickConfirm = {
                resetDialogState()
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.navigate(Screen.SignInScreen))
                }
            }
        )
    }


}