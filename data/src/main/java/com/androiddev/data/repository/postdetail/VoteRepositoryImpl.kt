package com.androiddev.data.repository.postdetail

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.postdetail.VoteApi
import com.androiddev.data.remote.dto.toGetVoteResponse
import com.androiddev.data.remote.dto.toVoteInfo
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.GetVoteResponse
import com.androiddev.domain.model.VoteInfo
import com.androiddev.domain.repository.postdetail.VoteRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class VoteRepositoryImpl @Inject constructor(
    private val api: VoteApi,
    private val context: Context
): VoteRepository {
    override suspend fun getVoteInfo(postId: Int): Flow<Resource<VoteInfo>> =
        safeApiCall(
            context = context,
            apiCall = { api.getVoteInfo(postId) },
            mapToResource = { it.toVoteInfo(
                isMyPost = it.isMyPost,
                hasVoted = it.hasVoted,
                selectedChoiceId = it.selectedChoiceId,
                voteOptions = it.voteOptions
            )}
        )

    override suspend fun vote(postId: Int, optionId: Int): Flow<Resource<VoteInfo>> =
        safeApiCall(
            context = context,
            apiCall = { api.vote(postId,optionId) },
            mapToResource = { it.toVoteInfo(
                isMyPost = it.isMyPost,
                hasVoted = it.hasVoted,
                selectedChoiceId = it.selectedChoiceId,
                voteOptions = it.voteOptions
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