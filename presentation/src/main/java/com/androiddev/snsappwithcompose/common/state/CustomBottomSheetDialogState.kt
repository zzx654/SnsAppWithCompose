package com.androiddev.snsappwithcompose.common.state

import com.androiddev.snsappwithcompose.common.model.BottomSheetItem

data class CustomBottomSheetDialogState(
    val showDialog:Boolean = false,
    val items:List<BottomSheetItem> = listOf(),
    val onClickCancel: () -> Unit = {},
)
data class BottomSheetDialogState<T>(
    val showDialog: Boolean = false,
    val options: List<T> = emptyList(),
    val onOptionSelected: (T) -> Unit = {},
    val onClickCancel: () -> Unit = {}
)