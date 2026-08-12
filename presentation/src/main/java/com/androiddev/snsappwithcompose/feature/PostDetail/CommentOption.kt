package com.androiddev.snsappwithcompose.feature.PostDetail

import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.model.BottomSheetOption

enum class CommentOption(
    override val iconRes: Int,
    override val titleRes: Int
) : BottomSheetOption {
    Edit(R.drawable.outline_edit, R.string.edit),
    Delete(R.drawable.outline_delete, R.string.delete),
    Report(R.drawable.outline_report, R.string.report),
    Block(R.drawable.outline_block, R.string.block_user),
    RequestChat(R.drawable.outline_chat, R.string.request_chat)
}