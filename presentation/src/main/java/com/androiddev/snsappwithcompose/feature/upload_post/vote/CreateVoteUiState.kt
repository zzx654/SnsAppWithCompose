package com.androiddev.snsappwithcompose.feature.upload_post.vote

data class CreateVoteUiState(
    val showBottomVoteDialog: Boolean = false,
    val saved: Boolean = false,
    val voteOptions: List<String> = List(3) { "" },
    val savedVoteOptions: List<String> = emptyList()
)