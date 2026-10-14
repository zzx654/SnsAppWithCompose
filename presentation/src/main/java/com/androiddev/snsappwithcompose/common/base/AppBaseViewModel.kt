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

    fun emitUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }
    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }
    suspend fun setEvent(event: UiEvent) = withContext(Dispatchers.Main) {
        _eventFlow.emit(event)
    }
    protected inline fun <T> Resource<T>.handle(
        onLoading: () -> Unit = { setLoading(true) },
        onSuccess: (T) -> Unit = {},
        onSuccessUnit:() -> Unit ={},
        onError: (UiText) -> Unit = { uiText -> emitUiEvent(UiEvent.ShowToast(uiText)) },
        onTokenExpired: () -> Unit = { emitUiEvent(UiEvent.navigate(screen = Screen.SignInScreen)) },
        onFinally: () -> Unit = { setLoading(false) }
    ): Resource<T> {

        when (this) {
            is Resource.Loading -> onLoading()
            is Resource.Success -> {
                onFinally() //
                data?.let {
                    onSuccess(it)
                }?:onSuccessUnit()
            }
            is Resource.Error -> {
                onFinally() //
                val errorUiText = error?.toUiText()
                    ?: message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.error)

                onError(errorUiText)
            }
            is Resource.TokenExpired -> {
                onFinally() //
                onTokenExpired()
            }
        }
        return this
    }
}