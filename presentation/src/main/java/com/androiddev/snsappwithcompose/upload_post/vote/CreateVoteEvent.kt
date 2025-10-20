package com.androiddev.snsappwithcompose.upload_post.vote

sealed class CreateVoteEvent {
    data object OnAddVoteClick: CreateVoteEvent()
    data object OnAddVoteOptionClick: CreateVoteEvent()
    data object onCancelClick: CreateVoteEvent()
    data class TypeVoteOption(val index:Int, val option: String): CreateVoteEvent()
    data object SaveVoteOptions: CreateVoteEvent()

}