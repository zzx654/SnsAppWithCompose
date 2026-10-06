package com.androiddev.snsappwithcompose.feature.auth.signup

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.androiddev.snsappwithcompose.common.util.Constants
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import com.androiddev.snsappwithcompose.common.state.AlertDialogStateV2
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class AuthViewModel: BaseViewModel() {
    protected val _authCodeField = mutableStateOf(AuthTextFieldState())
    val authCodeField: State<AuthTextFieldState>
        get() = _authCodeField
    protected val _isLoading = mutableStateOf(false)

    protected val _isCodeReceived = MutableStateFlow(false)
    val isCodeReceived = _isCodeReceived.asStateFlow()
    protected val _limitTime = MutableStateFlow(Constants.AUTH_LIMITEDTIME)
    val limitTime = _limitTime.asStateFlow()

    var timerJob: Job? = null

    protected val _alertDialogState = MutableStateFlow(AlertDialogStateV2())
    val alertDialogState: StateFlow<AlertDialogStateV2> = _alertDialogState.asStateFlow()
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
        _alertDialogState.value = AlertDialogStateV2()
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

}