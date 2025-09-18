package com.androiddev.domain.repository

import com.androiddev.domain.model.GetVoteResponse
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface VoteRepository {
    suspend fun getVoteInfo(postId:Int): Flow<Resource<GetVoteResponse>>
    suspend fun vote(postId:Int,optionId:Int): Flow<Resource<GetVoteResponse>>
    suspend fun cancelVote(postId:Int): Flow<Resource<Boolean>>
}