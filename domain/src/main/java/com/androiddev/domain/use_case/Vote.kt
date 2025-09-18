package com.androiddev.domain.use_case

import com.androiddev.domain.model.GetVoteResponse
import com.androiddev.domain.repository.VoteRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Vote @Inject constructor(
    private val repository: VoteRepository
) {
    suspend operator fun invoke(postId:Int,optionId:Int): Flow<Resource<GetVoteResponse>> = repository.vote(postId,optionId)

}