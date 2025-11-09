package com.androiddev.snsappwithcompose.feature.PostDetail.vote

sealed class VoteEvent{
    data object OnVoteClick: VoteEvent()
    data class  SelectOption(val optionId: Int): VoteEvent()
}