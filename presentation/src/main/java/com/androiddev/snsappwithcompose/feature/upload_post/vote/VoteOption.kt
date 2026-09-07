package com.androiddev.snsappwithcompose.feature.upload_post.vote

import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.model.BottomSheetOption

enum class VoteOption(
    override val iconRes: Int,
    override val titleRes: Int
) : BottomSheetOption {
    Edit(R.drawable.outline_edit, R.string.option_modify),
    Delete(R.drawable.delete, R.string.option_delete)
}