package com.androiddev.data.remote.dto

import com.androiddev.domain.model.VoteInfo
import com.androiddev.domain.model.VoteOptionInfo

data class VoteInfoDto(
    val isMyPost: Boolean,
    val hasVoted: Boolean,
    val selectedChoiceId: Int?,
    val voteOptions: List<VoteOptionInfo>
)
fun VoteInfoDto.toVoteInfo(
): VoteInfo = VoteInfo(
    isMyPost = isMyPost,
    hasVoted = hasVoted,
    selectedChoiceId = selectedChoiceId,
    voteOptions = voteOptions
)