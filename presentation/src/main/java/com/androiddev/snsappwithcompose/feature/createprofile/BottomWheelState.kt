package com.androiddev.snsappwithcompose.feature.createprofile

data class BottomWheelState(
    val showDialog:Boolean = false,
    val items:List<Int> = listOf(),
    val onClickConfirm: () -> Unit = {},
    val onClickCancel: () -> Unit = {},
)