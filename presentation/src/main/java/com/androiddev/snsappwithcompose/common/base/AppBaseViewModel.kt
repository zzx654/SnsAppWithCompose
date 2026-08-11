package com.androiddev.snsappwithcompose.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.common.util.toUiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    suspend fun setEvent(event: UiEvent) = withContext(Dispatchers.Main) {
        _eventFlow.emit(event)
    }
    protected suspend fun <T> handleResource(
        resource: Resource<T>,
        onSuccess: suspend (T) -> Unit = {},
        onSuccessUnit: suspend () -> Unit = {},
        onError: (suspend () -> Unit)? = null,
        onTokenExpired: (suspend () -> Unit)? = null
    ) {
        when (resource) {
            is Resource.Success -> {
                setLoading(false)
                resource.data?.let {
                    onSuccess(it)
                } ?: run {
                    onSuccessUnit()
                }
            }
            is Resource.Loading -> setLoading(true)
            is Resource.Error -> {
                setLoading(false)
                onError?.invoke()
                //  ?: setEvent(
                //     UiEvent.ShowToast(resource.message ?: getString(
                //    R.string.error))
                //)
                return
            }
            is Resource.TokenExpired -> {
                // 토큰 만료 시 공통 처리
                setLoading(false)
                onTokenExpired?.invoke()?:   setEvent(
                    UiEvent.navigate(
                        screen = Screen.SignInScreen
                    )
                )
            }
        }
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


        setLoading(this is Resource.Loading)


        when (this) {
            is Resource.Success -> data?.let(onSuccess)
            is Resource.Error -> {

                //DataError가 들어왔으면 toUiText() 매퍼 실행
                //기존 String message만 넘어온 레거시 코드면 DynamicString으로 처리 (호환성 유지)
                //둘 다 없으면 기본 알 수 없는 에러 문자열 사용
                val errorUiText = error?.toUiText()
                    ?: message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.error)

                onError(errorUiText)
            }
            is Resource.TokenExpired -> onTokenExpired()
            is Resource.Loading -> { /* setLoading으로 처리 완료 */ }
        }

        return this

    }

}