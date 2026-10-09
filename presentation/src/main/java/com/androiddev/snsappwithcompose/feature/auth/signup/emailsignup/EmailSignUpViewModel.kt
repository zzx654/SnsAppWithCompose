package com.androiddev.snsappwithcompose.feature.auth.signup.emailsignup

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.use_case.signup.emailsignup.EmailSignUpUseCases
import com.androiddev.domain.use_case.signup.emailsignup.InvalidEmailException
import com.androiddev.snsappwithcompose.common.util.Constants
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.state.AlertDialogState
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.state.AlertDialogStateV2
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.feature.auth.signup.AuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailSignUpViewModel @Inject constructor(
    private val emailSignUpUseCases: EmailSignUpUseCases,
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

    val isPasswordMatching: Boolean
        get() = _password.value.isNotEmpty() &&
                _password.value == _repeatPw.value &&
                emailSignUpUseCases.validateSignUpPassword(_password.value)

    private fun requestAuthCode() {
        //  개별 이메일 검증 UseCase 사용
        if (emailSignUpUseCases.validateEmail(email.value)!=null) {
            viewModelScope.launch {
                setEvent(UiEvent.ShowToast(UiText.StringResource(R.string.check_email)))
            }
            return
        }
        viewModelScope.launch {
            emailSignUpUseCases.requestAuthCode(email.value).collect { result ->

                result.handle(
                    onSuccess = { data ->
                        if (!data.isValid) {
                            showEmailExistAlert()
                        } else {
                            _isCodeReceived.value = true
                            _limitTime.value = Constants.AUTH_LIMITEDTIME
                            _authCodeField.value = authCodeField.value.copy(code = "", isError = false)
                            timerStart()
                        }
                    }
                )
            }
        }
    }

    private fun performEmailSignUp(phoneNumber: String) {
        viewModelScope.launch {
            emailSignUpUseCases.emailSignUp(
                account = email.value,
                password = password.value,
                phonenumber = phoneNumber,
                authCode = authCodeField.value.code
            ).collect { result ->
                result.handle(
                    onSuccess = { data ->
                        if (data.isCorrect) {
                            showSignUpCompletedAlert()
                        } else {
                            showWrongCodeAlert()
                        }
                    }
                )
            }
        }
    }
    fun onEvent(event: EmailSignUpEvent) {
        when(event) {
            is EmailSignUpEvent.TypeEmail -> {
                _email.value = event.email
                if (isCodeReceived.value) {
                    _isCodeReceived.value = false
                    timerJob?.cancel()
                }
            }
            is EmailSignUpEvent.RequestAuthCode -> requestAuthCode()
            is EmailSignUpEvent.EmailSignUp -> performEmailSignUp(event.phonenumber)

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
    private fun showEmailExistAlert() {
        _alertDialogState.value = AlertDialogStateV2(
            title = UiText.StringResource(R.string.email_exist),
            confirmText = UiText.StringResource(R.string.confirm),
            onClickConfirm = {
                resetDialogState()
            }
        )
    }
    private fun showWrongCodeAlert() {
        _alertDialogState.value = AlertDialogStateV2(
            title = UiText.StringResource(R.string.wrong_code),
            confirmText = UiText.StringResource(R.string.confirm),
            onClickConfirm = {
                resetDialogState()
            }
        )
    }
    private fun showSignUpCompletedAlert() {
        _alertDialogState.value = AlertDialogStateV2(
            title = UiText.StringResource(R.string.signup_completed),
            confirmText = UiText.StringResource(R.string.confirm),
            onClickConfirm = {
                resetDialogState()
                viewModelScope.launch {
                    setEvent(UiEvent.navigate(Screen.SignInScreen))
                }
            }
        )
    }
}