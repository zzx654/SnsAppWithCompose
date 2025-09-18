package com.androiddev.domain.use_case

data class VoteUseCases(
    val getVoteInfo: GetVoteInfo,
    val vote: Vote,
    val cancelVote: CancelVote
)