package com.androiddev.domain.repository.postdetail

import com.androiddev.domain.model.VoteInfo
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface VoteRepository {
    suspend fun getVoteInfo(postId:Int): Flow<Resource<VoteInfo>>
    suspend fun vote(postId:Int,optionId:Int): Flow<Resource<VoteInfo>>
    suspend fun cancelVote(postId:Int): Flow<Resource<Unit>>
}