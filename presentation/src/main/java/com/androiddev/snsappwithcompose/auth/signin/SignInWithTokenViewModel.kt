package com.androiddev.snsappwithcompose.auth.signin

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddev.data.util.UserPreferences
import com.androiddev.domain.use_case.SignInUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.AlertDialogState
import com.androiddev.snsappwithcompose.util.Screen
import com.androiddev.snsappwithcompose.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInWithTokenViewModel @Inject constructor(
    private val signInUseCases: SignInUseCases,
    private val context: Context
) : ViewModel() {
    private val _isLoading = mutableStateOf(false)
    val isLoading : State<Boolean>
        get() = _isLoading
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()
    private val _alertDialogState: MutableState<AlertDialogState> = mutableStateOf(AlertDialogState())
    val alertDialogState: State<AlertDialogState>
        get() = _alertDialogState

    init {
        signInWithToken()
    }
    fun signInWithToken() {
        viewModelScope.launch {
            signInUseCases.signInWithToken()
                .collect{ result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let {
                                if(it.signInResult) {
                                    if(it.profileWritten) {
                                      //홈화면
                                        _eventFlow.emit(
                                            UiEvent.navigate(
                                                screen = Screen.HomeScreen
                                            )
                                        )
                                    } else {
                                        //프로필화면
                                        _eventFlow.emit(
                                            UiEvent.navigate(
                                                screen = Screen.CreateprofileScreen
                                            )
                                        )
                                    }
                                } else {
                                    // 로그인시작화면으로 가기
                                    _eventFlow.emit(
                                        UiEvent.navigate(
                                            screen = Screen.SignInScreen
                                        )
                                    )
                                }
                            }
                        }
                        is Resource.Error -> {
                            _isLoading.value = false
                            showSignInFialedAlert(result.message)

                        }
                        is Resource.Loading -> {
                            _isLoading.value = true
                        }
                    }
                }
        }
    }
    private fun showSignInFialedAlert(message:String?) {
        _alertDialogState.value = AlertDialogState(
            title = message?: getString(context,R.string.error),
            confirmText = getString(context, R.string.retry),
            onClickConfirm = {
                resetDialogState()
                signInWithToken()
            }
        )
    }
    protected fun resetDialogState() {
        _alertDialogState.value = AlertDialogState()
    }
}