package com.androiddev.snsappwithcompose.util

data class AlertDialogState(
    val title: String = "",
    val cancelText: String = "",
    val confirmText: String = "",
    val onClickConfirm: () -> Unit = {},
    val onClickCancel: () -> Unit = {}
)