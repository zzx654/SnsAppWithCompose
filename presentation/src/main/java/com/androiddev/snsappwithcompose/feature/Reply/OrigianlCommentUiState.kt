package com.androiddev.snsappwithcompose.feature.Reply

import com.androiddev.domain.model.Comment
import com.androiddev.snsappwithcompose.common.mapper.CommentUiState
import com.androiddev.snsappwithcompose.common.mapper.toUiState

data class OrigianlCommentUiState (
    val comment: Comment? = null,
    val isLoading:Boolean = true
) {
    val commentUiState:CommentUiState?
        get() = comment?.toUiState()
    val isCommentLoading: Boolean
        get() {
            return comment == null || isLoading
        }
}