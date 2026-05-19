package com.androiddev.data.repository.postdetail

import android.content.Context
import com.androiddev.data.remote.api.postdetail.VoteApi
import com.androiddev.data.remote.dto.toVoteInfo
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.VoteInfo
import com.androiddev.domain.repository.postdetail.VoteRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VoteRepositoryImpl @Inject constructor(
    private val api: VoteApi,
    private val context: Context
): VoteRepository {
    override suspend fun getVoteInfo(postId: Int): Flow<Resource<VoteInfo>> =
        safeApiCall(
            context = context,
            apiCall = { api.getVoteInfo(postId) },
            mapToResource = { it.toVoteInfo()}
        )

    override suspend fun vote(postId: Int, optionId: Int): Flow<Resource<VoteInfo>> =
        safeApiCall(
            context = context,
            apiCall = { api.vote(postId,optionId) },
            mapToResource = { it.toVoteInfo(
            )}
        )

    override suspend fun cancelVote(postId: Int): Flow<Resource<Unit>> =
        safeApiCall(
            context = context,
            apiCall = {
                api.cancelVote(postId)
            },
            mapToResource = {}
        )

}