package com.androiddev.snsappwithcompose.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class RadioButtonsState(
    val text:String,
    val isChecked:MutableState<Boolean> = mutableStateOf(false)
)