package com.androiddev.snsappwithcompose.PostDetail

sealed class VoteEvent{
    data object Vote: VoteEvent()
    data class  SelectOption(val optionId: Int): VoteEvent()
}