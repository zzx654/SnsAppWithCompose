package com.androiddev.domain.model

data class GetVoteResponse(
    val isTokenValid: Boolean,
    val isMyPost: Boolean,
    val hasVoted: Boolean,
    val selectedChoiceId: Int?,
    val voteInfo: List<VoteInfo>
)