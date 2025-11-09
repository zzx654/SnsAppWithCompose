package com.androiddev.snsappwithcompose.feature.upload_post.record

data class BottomRecordState(
    val showDialog:Boolean = false,
    val onClickConfirm: () -> Unit = {},
    val onClickCancel: () -> Unit = {},
)