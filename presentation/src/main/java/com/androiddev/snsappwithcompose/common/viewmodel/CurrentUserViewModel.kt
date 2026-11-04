package com.androiddev.snsappwithcompose.common.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class CurrentUserViewModel @Inject constructor(): ViewModel() {
    private val _userId: MutableStateFlow<Long?> = MutableStateFlow(null)
    val userId: StateFlow<Long?> = _userId.asStateFlow()
    // 로그인 완료 여부
    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn
    var isProfileWritten: Boolean = false


    fun setUserId(id: Long){
        _userId.value = id
        _isSignedIn.value = true
    }
}