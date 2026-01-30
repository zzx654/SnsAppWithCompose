package com.androiddev.snsappwithcompose.common.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class CurrentUserViewModel @Inject constructor(): ViewModel() {
    private val _userId: MutableState<Int> = mutableStateOf(0)
    // 로그인 완료 여부
    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn
    var isProfileWritten: Boolean = false
    val userId: State<Int>
        get() = _userId

    fun setUserId(id: Int){
        _userId.value = id
    }
}