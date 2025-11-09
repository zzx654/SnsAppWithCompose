package com.androiddev.snsappwithcompose.common.state

import com.androiddev.snsappwithcompose.common.model.BottomSheetItem

data class CustomBottomSheetDialogState(
    val showDialog:Boolean = false,
    val items:List<BottomSheetItem> = listOf(),
    val onClickCancel: () -> Unit = {},
)