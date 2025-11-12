package com.androiddev.domain.use_case.postdetail

import com.androiddev.domain.repository.postdetail.VoteRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CancelVote @Inject constructor(
    private val repository: VoteRepository
) {
    suspend operator fun invoke(postId:Int): Flow<Resource<Unit>> = repository.cancelVote(postId)

}