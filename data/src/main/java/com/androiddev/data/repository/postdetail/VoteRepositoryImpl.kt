package com.androiddev.data.repository.postdetail

import android.content.Context
import com.androiddev.data.remote.api.postdetail.VoteApi
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.VoteInfo
import com.androiddev.domain.repository.postdetail.VoteRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VoteRepositoryImpl @Inject constructor(
    private val api: VoteApi,
): VoteRepository {
    override suspend fun getVoteInfo(postId: Int): Flow<Resource<VoteInfo>> =
        safeApiCall(
            apiCall = { api.getVoteInfo(postId) },
            mapToResource = { it.toDomain()}
        )

    override suspend fun vote(postId: Int, optionId: Int): Flow<Resource<VoteInfo>> =
        safeApiCall(
            apiCall = { api.vote(postId,optionId) },
            mapToResource = { it.toDomain(
            )}
        )

    override suspend fun cancelVote(postId: Int): Flow<Resource<Unit>> =
        safeApiCall(
            apiCall = {
                api.cancelVote(postId)
            },
            mapToResource = {}
        )

}