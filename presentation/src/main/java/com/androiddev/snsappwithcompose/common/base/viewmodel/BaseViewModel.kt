package com.androiddev.snsappwithcompose.common.base.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.state.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject


abstract class BaseViewModel (protected val context: Context): ViewModel() {
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _isLoading = mutableStateOf(false)
    val isLoading : State<Boolean>
        get() = _isLoading
    suspend fun setEvent(event: UiEvent) = withContext(Dispatchers.Main) {
        _eventFlow.emit(event)
    }
    suspend fun setLoading(isLoading: Boolean) = withContext(Dispatchers.Main) {
        _isLoading.value = isLoading
    }
    protected fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }
    protected suspend fun <T> handleResource(
        resource: Resource<T>,
        onSuccess: suspend (T) -> Unit,
        onError: (suspend () -> Unit)? = null,
        onTokenExpired: (suspend () -> Unit)? = null
    ) {
        when (resource) {
            is Resource.Success -> {
                setLoading(false)
                resource.data?.let {
                    onSuccess(it)
                }
            }
            is Resource.Loading -> setLoading(true)
            is Resource.Error -> {
                setLoading(false)
                onError?.invoke()
                    ?: setEvent(UiEvent.ShowToast(resource.message ?: getString(
                        R.string.error))
                    )
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
}