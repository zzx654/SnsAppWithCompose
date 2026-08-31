package com.androiddev.snsappwithcompose.common.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class CurrentUserViewModel @Inject constructor(): ViewModel() {
    private val _userId: MutableStateFlow<Int?> = MutableStateFlow(null)
    val userId: StateFlow<Int?> = _userId.asStateFlow()
    // 로그인 완료 여부
    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn
    var isProfileWritten: Boolean = false


    fun setUserId(id: Int){
        _userId.value = id
    }
}