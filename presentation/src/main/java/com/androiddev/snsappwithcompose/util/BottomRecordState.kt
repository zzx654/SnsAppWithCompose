package com.androiddev.snsappwithcompose.util

data class BottomRecordState(
    val showDialog:Boolean = false,
    val onClickConfirm: () -> Unit = {},
    val onClickCancel: () -> Unit = {},
)