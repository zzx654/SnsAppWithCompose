package com.androiddev.domain.model

data class VoteOptionInfo(
    val optionId: Long,
    val optionText: String,
    val voteCount: Int,
    val percentage: Double
)