package com.androiddev.snsappwithcompose.util

data class CustomBottomSheetDialogState(
    val showDialog:Boolean = false,
    val items:List<BottomSheetItem> = listOf(),
    val onClickCancel: () -> Unit = {},
)