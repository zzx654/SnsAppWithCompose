package com.androiddev.data.remote.dto

import com.androiddev.domain.model.GetVoteResponse
import com.androiddev.domain.model.VoteInfo

data class GetVoteResponseDto(
    val isTokenValid: Boolean,
    val resultCode: Int,
    val isMyPost: Boolean,
    val hasVoted: Boolean,
    val selectedChoiceId: Int?,
    val voteInfo: List<VoteInfo>
)
fun GetVoteResponseDto.toGetVoteResponse(
    isTokenValid: Boolean,
    isMyPost: Boolean,
    hasVoted: Boolean,
    selectedChoiceId: Int?,
    voteInfo: List<VoteInfo>
): GetVoteResponse = GetVoteResponse(
    isTokenValid = isTokenValid,
    isMyPost = isMyPost,
    hasVoted = hasVoted,
    selectedChoiceId = selectedChoiceId,
    voteInfo = voteInfo
)