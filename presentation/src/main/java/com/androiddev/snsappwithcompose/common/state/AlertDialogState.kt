package com.androiddev.snsappwithcompose.common.state

data class AlertDialogState(
    val title: String = "",
    val cancelText: String = "",
    val confirmText: String = "",
    val onClickConfirm: () -> Unit = {},
    val onClickCancel: () -> Unit = {}
)