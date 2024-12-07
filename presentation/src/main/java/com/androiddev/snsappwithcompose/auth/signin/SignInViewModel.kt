package com.androiddev.snsappwithcompose.auth.signin

import android.content.Context
import android.content.res.Resources
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.use_case.SignInUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.auth.util.UserPreferences
import com.androiddev.snsappwithcompose.util.Screen
import com.androiddev.snsappwithcompose.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCases: SignInUseCases,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _isLoading = mutableStateOf(false)
    val isLoading : State<Boolean>
        get() = _isLoading
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: SignInEvent) {
        when(event) {
            is SignInEvent.socialSignIn -> {
                viewModelScope.launch {
                    signInUseCases.socialSignIn(event.platform,event.account)
                        .collect { result ->
                            when(result) {
                                is Resource.Success -> {
                                    _isLoading.value = false
                                    result.data?.let {
                                        if(it.isMember) {
                                            //가입된 계정일때
                                            userPreferences.saveAuthToken(it.token)
                                            if(it.profileWritten) {
                                                //홈화면으로 이동

                                            }
                                            else {
                                                //프로필 작성화면으로이동
                                            }
                                        } else {
                                            //가입안된 계정일때 핸드폰 인증화면으로 이동
                                            _eventFlow.emit(
                                                UiEvent.navigate(
                                                    screen = Screen.AuthPhoneScreen(event.platform,event.account)
                                                )
                                            )
                                        }
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
        }

    }

}