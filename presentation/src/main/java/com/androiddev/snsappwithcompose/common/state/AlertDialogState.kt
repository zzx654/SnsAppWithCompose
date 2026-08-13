package com.androiddev.snsappwithcompose.common.state

import com.androiddev.snsappwithcompose.common.util.UiText

data class AlertDialogState(
    val title: String = "",
    val cancelText: String = "",
    val confirmText: String = "",
    val onClickConfirm: () -> Unit = {},
    val onClickCancel: () -> Unit = {}
)
data class AlertDialogStateV2(
    val title: UiText? = null,
    val confirmText: UiText? = null,
    val cancelText: UiText? = null,
    val onClickConfirm: () -> Unit = {},
    val onClickCancel: () -> Unit = {}
)