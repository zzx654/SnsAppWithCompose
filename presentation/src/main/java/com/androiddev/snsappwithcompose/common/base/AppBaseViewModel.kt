package com.androiddev.snsappwithcompose.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.util.UiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel(), UiStateProvider {



    private val _eventFlow = MutableSharedFlow<UiEvent>()
    override val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()



    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()



    protected fun emitUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

    protected fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    protected inline fun <T> Resource<T>.handle(

        onSuccess: (T) -> Unit = {},
        onError: (UiText) -> Unit = { uiText ->
            emitUiEvent(UiEvent.ShowToast(uiText)) },
        onTokenExpired: () -> Unit = {

// 기본 토큰 만료 처리

            emitUiEvent(UiEvent.navigate(

                screen = Screen.SignInScreen

            ))

        }

    ): Resource<T> {

// Loading 여부 자동 처리 (Resource.Loading이면 true, 나머지는 false)

        setLoading(this is Resource.Loading)



// 각 상태별 액션 실행

        when (this) {
            is Resource.Success -> data?.let(onSuccess)
            is Resource.Error -> {
                // 서버가 보내준 message가 있으면 DynamicString으로 처리
                // 메시지가 null이면 앱 내부 R.string.error_unknown 리소스 ID 사용
                val errorUiText = message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.error)

                onError(errorUiText)
            }
            is Resource.TokenExpired -> onTokenExpired()
            is Resource.Loading -> { /* setLoading으로 처리 완료 */ }
        }

        return this

    }

}