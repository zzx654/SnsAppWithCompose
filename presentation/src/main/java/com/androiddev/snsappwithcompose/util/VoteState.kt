package com.androiddev.snsappwithcompose.util

import com.androiddev.domain.model.VoteInfo

data class VoteState(
    val isMyPost: Boolean = false,
    val hasVoted: Boolean = false,
    val selectedChoiceId: Int? = null,
    val voteInfo: List<VoteInfo> = listOf()
)