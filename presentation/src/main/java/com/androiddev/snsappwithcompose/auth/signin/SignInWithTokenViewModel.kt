package com.androiddev.snsappwithcompose.auth.signin

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.use_case.SignInUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.AlertDialogState
import com.androiddev.snsappwithcompose.util.BaseViewModel
import com.androiddev.snsappwithcompose.navigation.components.Screen
import com.androiddev.snsappwithcompose.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInWithTokenViewModel @Inject constructor(
    private val signInUseCases: SignInUseCases,
    private val context: Context
) : BaseViewModel() {

    private val _alertDialogState: MutableState<AlertDialogState> = mutableStateOf(AlertDialogState())
    val alertDialogState: State<AlertDialogState>
        get() = _alertDialogState

    init {

            signInWithToken()


    }
    fun signInWithToken() {
        viewModelScope.launch(Dispatchers.Default) {
            signInUseCases.signInWithToken()
                .collect{ result ->

                    withContext(Dispatchers.IO) {
                        when(result) {
                            is Resource.Success -> {
                                setLoading(false)
                                result.data?.let {
                                    if(it.signInResult) {
                                        if(it.profileWritten) {
                                            //홈화면
                                            setEvent(
                                                UiEvent.navigate(
                                                    screen = Screen.MainScreen
                                                )
                                            )
                                        } else {
                                            //프로필화면
                                            setEvent(
                                                UiEvent.navigate(
                                                    screen = Screen.CreateprofileScreen
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
                                }
                            }
                            is Resource.Error -> {
                                setLoading(false)
                                showSignInFialedAlert(result.message)

                            }
                            is Resource.Loading -> {
                                setLoading(true)
                            }
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