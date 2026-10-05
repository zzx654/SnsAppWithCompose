package com.androiddev.snsappwithcompose.feature.auth.init


import androidx.lifecycle.viewModelScope
import com.androiddev.domain.use_case.signin.SignInUseCases
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.state.AlertDialogStateV2
import com.androiddev.snsappwithcompose.common.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInWithTokenViewModel @Inject constructor(
    private val signInUseCases: SignInUseCases
) : BaseViewModel() {

    private val _alertDialogState = MutableStateFlow(AlertDialogStateV2())
    val alertDialogState: StateFlow<AlertDialogStateV2> = _alertDialogState.asStateFlow()


    init {
        signInWithToken()
    }
    fun signInWithToken() {
        viewModelScope.launch {
            signInUseCases.signInWithToken().collect { result->
                launch {
                    result.handle(
                        onSuccess = { data ->
                            if (data.signInResult) {

                                if (data.profileWritten) {
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
                                            userId = data.userId
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
                        onError = { showSignInFailedAlert(result.message) },
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
    private fun showSignInFailedAlert(message:String?) {
        _alertDialogState.value = AlertDialogStateV2(
            title = message?.let{ UiText.DynamicString(it) }?: UiText.StringResource(R.string.error),
            confirmText = UiText.StringResource(R.string.retry),
            onClickConfirm = {
                resetDialogState()
                signInWithToken()
            }
        )
    }
    protected fun resetDialogState() {
        _alertDialogState.value = AlertDialogStateV2()
    }
}