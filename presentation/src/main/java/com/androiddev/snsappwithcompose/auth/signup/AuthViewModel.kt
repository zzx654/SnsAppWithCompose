package com.androiddev.snsappwithcompose.auth.signup

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.androiddev.snsappwithcompose.Constants
import com.androiddev.snsappwithcompose.util.AlertDialogState
import com.androiddev.snsappwithcompose.util.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class AuthViewModel(): BaseViewModel() {
    protected val _authCodeField = mutableStateOf(AuthTextFieldState())
    val authCodeField: State<AuthTextFieldState>
        get() = _authCodeField
    protected val _isLoading = mutableStateOf(false)

    protected val _isCodeReceived = mutableStateOf(false)
    val isCodeReceived: State<Boolean>
        get() = _isCodeReceived
    protected val _limitTime = MutableStateFlow(Constants.AUTH_LIMITEDTIME)
    val limitTime = _limitTime.asStateFlow()

    var timerJob: Job? = null

    protected val _alertDialogState: MutableState<AlertDialogState> = mutableStateOf(AlertDialogState())
    val alertDialogState: State<AlertDialogState>
        get() = _alertDialogState
    protected fun timerStart() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while(limitTime.value >=0 && isCodeReceived.value) {
                if(limitTime.value == 0)
                    _isCodeReceived.value = false
                else {
                    delay(1000L)
                    _limitTime.value--
                }
            }
        }
    }
    protected fun resetDialogState() {
        _alertDialogState.value = AlertDialogState()
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

}