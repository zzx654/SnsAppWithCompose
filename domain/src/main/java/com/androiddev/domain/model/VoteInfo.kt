package com.androiddev.domain.model

data class VoteInfo(
    val optionId: Int,
    val optionText: String,
    val voteCount: Int,
    val percentage: Double
)