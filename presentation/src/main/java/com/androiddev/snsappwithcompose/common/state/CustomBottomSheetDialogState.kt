package com.androiddev.snsappwithcompose.common.state


data class BottomSheetDialogState<T>(
    val showDialog: Boolean = false,
    val options: List<T> = emptyList(),
    val onOptionSelected: (T) -> Unit = {},
    val onClickCancel: () -> Unit = {}
)