package com.androiddev.domain.repository.postdetail

import com.androiddev.domain.model.VoteInfo
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface VoteRepository {
    suspend fun getVoteInfo(postId: Long): Flow<Resource<VoteInfo>>
    suspend fun vote(postId:Long,optionId:Long): Flow<Resource<VoteInfo>>
    suspend fun cancelVote(postId: Long): Flow<Resource<Unit>>
}