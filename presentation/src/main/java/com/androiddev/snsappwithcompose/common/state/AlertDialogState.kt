package com.androiddev.snsappwithcompose.common.state

import com.androiddev.snsappwithcompose.common.util.UiText

data class AlertDialogState(
    val title: UiText? = null,
    val confirmText: UiText? = null,
    val cancelText: UiText? = null,
    val onClickConfirm: () -> Unit = {},
    val onClickCancel: () -> Unit = {}
)