package com.androiddev.snsappwithcompose.feature.auth.init

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.model.SigninWithTokenResult
import com.androiddev.domain.use_case.signin.SignInUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.state.AlertDialogState
import com.androiddev.snsappwithcompose.common.base.viewmodel.BaseViewModel
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.state.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInWithTokenViewModel @Inject constructor(
    private val signInUseCases: SignInUseCases,
    @ApplicationContext context: Context,
) : BaseViewModel(context) {

    private val _alertDialogState: MutableState<AlertDialogState> = mutableStateOf(AlertDialogState())
    val alertDialogState: State<AlertDialogState>
        get() = _alertDialogState

    init {
        signInWithToken()
    }
    fun signInWithToken() {
        viewModelScope.launch {
            signInUseCases.signInWithToken().collect { result->
                launch {
                    handleResource(
                        resource = result,
                        onSuccess = { data ->
                            if(data.signInResult) {

                                if(data.profileWritten) {
                                    //홈화면
                                    setEvent(
                                        UiEvent.navigate(
                                            screen = Screen.MainScreen,
                                            userId = data.userId
                                        )
                                    )
                                } else {
                                    //프로필화면
                                    setEvent(
                                        UiEvent.navigate(
                                            screen = Screen.CreateprofileScreen,
                                            userId=  data.userId
                                        )
                                    )
                                }
                            } else {
                                // 로그인시작화면으로 가기
                                setEvent(
                                    UiEvent.navigate(
                                        screen = Screen.SignInScreen
                                    )
                                )
                            }
                        },
                        onError = { showSignInFialedAlert(result.message) },
                        onTokenExpired = {
                            setEvent(
                                UiEvent.navigate(
                                    screen = Screen.SignInScreen
                                )
                            )
                        },
                    )
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