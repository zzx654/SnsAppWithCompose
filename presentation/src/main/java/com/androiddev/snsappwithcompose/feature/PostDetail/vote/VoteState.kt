package com.androiddev.snsappwithcompose.feature.PostDetail.vote

import com.androiddev.domain.model.VoteOptionInfo

data class VoteState(
    val isMyPost: Boolean = false,
    val hasVoted: Boolean = false,
    val selectedChoiceId: Int? = null,
    val voteOptions: List<VoteOptionInfo> = listOf()
)