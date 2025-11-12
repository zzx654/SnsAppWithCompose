package com.androiddev.domain.model

data class VoteInfo(
    val isMyPost: Boolean,
    val hasVoted: Boolean,
    val selectedChoiceId: Int?,
    val voteOptions:List<VoteOptionInfo>
)