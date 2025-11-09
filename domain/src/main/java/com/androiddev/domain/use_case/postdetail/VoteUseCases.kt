package com.androiddev.domain.use_case.postdetail

data class VoteUseCases(
    val getVoteInfo: GetVoteInfo,
    val vote: Vote,
    val cancelVote: CancelVote
)