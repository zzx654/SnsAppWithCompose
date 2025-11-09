package com.androiddev.snsappwithcompose.feature.upload_post.vote

import com.androiddev.snsappwithcompose.feature.upload_post.PostMode


sealed class CreateVoteEvent {
    data class OnAddVoteClick(val postMode: PostMode): CreateVoteEvent()
    data object OnAddVoteOptionClick: CreateVoteEvent()
    data object onCancelClick: CreateVoteEvent()
    data class TypeVoteOption(val index:Int, val option: String): CreateVoteEvent()
    data object SaveVoteOptions: CreateVoteEvent()

}