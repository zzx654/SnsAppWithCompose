package com.androiddev.domain.model

data class VoteInfo(
    val isMyPost: Boolean,
    val hasVoted: Boolean,
    val selectedChoiceId: Long?,
    val voteOptions:List<VoteOptionInfo>
)