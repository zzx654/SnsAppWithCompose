package com.androiddev.data.repository

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.CommentApi
import com.androiddev.data.remote.dto.toGetCommentsResponse
import com.androiddev.data.remote.dto.toToggleLikeResponse
import com.androiddev.domain.model.GetCommentsResponse
import com.androiddev.domain.model.ToggleLikeResponse
import com.androiddev.domain.repository.CommentRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val api: CommentApi,
    private val context: Context
): CommentRepository {
    override suspend fun getPopularComments(
        postId: Int,
        commentId: Int?,
        score: Int
    ): Flow<Resource<GetCommentsResponse>> {
        return flow {
            try{
                emit(Resource.Loading())
                api.getPopularComments(postId,commentId,score).body()?.let { result ->
                    if(result.resultCode == 200) {
                        val getCommentsresult = result.toGetCommentsResponse(comments = result.comments, isTokenValid = result.isTokenValid)
                        emit(Resource.Success(getCommentsresult))
                    } else {
                        emit(Resource.Error(getString(context, R.string.server_error)))
                    }
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
    override suspend fun getComments(
        postId: Int,
        commentId: Int?,
        commentDate: String?
    ): Flow<Resource<GetCommentsResponse>> {
        return flow {
            try{
                emit(Resource.Loading())
                api.getComments(postId,commentId,commentDate).body()?.let { result ->
                    if(result.resultCode == 200) {
                        val getCommentsresult = result.toGetCommentsResponse(comments = result.comments, isTokenValid = result.isTokenValid)
                        emit(Resource.Success(getCommentsresult))
                    } else {
                        emit(Resource.Error(getString(context, R.string.server_error)))
                    }
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

    override suspend fun postComment(
        postId: Int,
        text: String,
        anonymousNick: String?
    ): Flow<Resource<GetCommentsResponse>> {
        return flow {
            try{
                emit(Resource.Loading())
                api.postComments(postId,text,anonymousNick).body()?.let { result ->
                    if(result.resultCode == 200) {
                        val getCommentsresult = result.toGetCommentsResponse(comments = result.comments, isTokenValid = result.isTokenValid)
                        emit(Resource.Success(getCommentsresult))
                    } else {
                        emit(Resource.Error(getString(context, R.string.server_error)))
                    }
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
    override suspend fun toggleLikeComment(commentId: Int): Flow<Resource<ToggleLikeResponse>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.toggleLikeComment(commentId).body()?.let{ result ->
                    if(result.resultCode == 200) {
                        emit(Resource.Success(result.toToggleLikeResponse(result.isLiked,result.isTokenValid)))
                    }
                    else
                        emit(Resource.Error(getString(context, R.string.server_error)))
                }
            } catch(e: HttpException) {
                emit(
                    Resource.Error(e.localizedMessage ?: getString(context,
                        R.string.unexpected_error)
                    ))

            } catch(e: IOException) {
                emit(Resource.Error(getString(context, R.string.connection_error)))
            }
        }
    }


}