package com.androiddev.snsappwithcompose.common.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State

@HiltViewModel
class UserViewModel @Inject constructor(): ViewModel() {
    private val _userId: MutableState<Int> = mutableStateOf(0)
    val userId: State<Int>
        get() = _userId

    fun setUserId(id: Int) {
        _userId.value = id
    }
}