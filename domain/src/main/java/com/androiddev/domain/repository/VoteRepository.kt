package com.androiddev.domain.repository

import com.androiddev.domain.model.GetVoteResponse
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface VoteRepository {
    suspend fun getVoteInfo(postid:Int): Flow<Resource<GetVoteResponse>>
}