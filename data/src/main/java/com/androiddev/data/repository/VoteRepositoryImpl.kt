package com.androiddev.data.repository

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.VoteApi
import com.androiddev.data.remote.dto.toGetVoteResponse
import com.androiddev.domain.model.CancelVoteResponse
import com.androiddev.domain.model.GetVoteResponse
import com.androiddev.domain.repository.VoteRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class VoteRepositoryImpl @Inject constructor(
    private val api: VoteApi,
    private val context: Context
):VoteRepository {
    override suspend fun getVoteInfo(postId: Int): Flow<Resource<GetVoteResponse>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.getVoteInfo(postId).body()?.let{ result ->
                    if(result.resultCode == 200) {
                        emit(Resource.Success(result.toGetVoteResponse(
                            isTokenValid = result.isTokenValid,
                            isMyPost = result.isMyPost,
                            hasVoted = result.hasVoted,
                            selectedChoiceId = result.selectedChoiceId,
                            voteInfo = result.voteInfo))
                        )
                    }
                    else
                        emit(Resource.Error(getString(context, R.string.server_error)))
                }
            } catch(e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: getString(context,
                    R.string.unexpected_error)
                ))

            } catch(e: IOException) {
                emit(Resource.Error(getString(context, R.string.connection_error)))
            }
        }
    }

    override suspend fun vote(postId: Int, optionId: Int): Flow<Resource<GetVoteResponse>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.vote(postId,optionId).body()?.let{ result ->
                    if(result.resultCode == 200) {
                        emit(Resource.Success(result.toGetVoteResponse(
                            isTokenValid = result.isTokenValid,
                            isMyPost = result.isMyPost,
                            hasVoted = result.hasVoted,
                            selectedChoiceId = result.selectedChoiceId,
                            voteInfo = result.voteInfo))
                        )
                    }
                    else
                        emit(Resource.Error(getString(context, R.string.server_error)))
                }
            } catch(e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: getString(context,
                    R.string.unexpected_error)
                ))

            } catch(e: IOException) {
                emit(Resource.Error(getString(context, R.string.connection_error)))
            }
        }
    }

    override suspend fun cancelVote(postId: Int): Flow<Resource<Boolean>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.cancelVote(postId).body()?.let{ result ->
                    if(result.resultCode == 200) {
                        emit(Resource.Success(result.isTokenValid))
                    }
                    else
                        emit(Resource.Error(getString(context, R.string.server_error)))
                }
            } catch(e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: getString(context,
                    R.string.unexpected_error)
                ))

            } catch(e: IOException) {
                emit(Resource.Error(getString(context, R.string.connection_error)))
            }
        }
    }

}