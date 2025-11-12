package com.androiddev.domain.model

data class VoteOptionInfo(
    val optionId: Int,
    val optionText: String,
    val voteCount: Int,
    val percentage: Double
)