package com.androiddev.snsappwithcompose.PostDetail

sealed class VoteEvent{
    data object OnVoteClick: VoteEvent()
    data class  SelectOption(val optionId: Int): VoteEvent()
}