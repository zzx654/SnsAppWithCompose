package com.androiddev.snsappwithcompose.util

data class BottomWheelState(
    val showDialog:Boolean = false,
    val items:List<Int> = listOf(),
    val onClickConfirm: () -> Unit = {},
    val onClickCancel: () -> Unit = {},
)