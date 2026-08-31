package com.androiddev.snsappwithcompose.common.mapper

import com.androiddev.domain.model.Comment
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.common.util.elapsedTime

fun Comment.toUiState():CommentUiState {
    val displayUserName = anonymousNickname?.let { nonNullAnonymous ->
        UiText.StringResource(R.string.anonymous_with_code, nonNullAnonymous)
    } ?: UiText.DynamicString(nickname)
    return CommentUiState (
        comment = this,
        displayUserName = displayUserName,
        elapsedTime = elapsedTime(this.date)
    )
}

data class CommentUiState (
    val comment: Comment,
    val elapsedTime: String,
    val displayUserName: UiText

)