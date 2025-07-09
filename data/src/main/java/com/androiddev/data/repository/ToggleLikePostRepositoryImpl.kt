package com.androiddev.data.repository

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.SignUpApi
import com.androiddev.data.remote.api.ToggleLikePostApi
import com.androiddev.data.remote.dto.toToggleLikePostResponse
import com.androiddev.domain.model.ToggleLikePostResponse
import com.androiddev.domain.repository.SignupRepository
import com.androiddev.domain.repository.ToggleLikePostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ToggleLikePostRepositoryImpl @Inject constructor(
    private val api: ToggleLikePostApi,
    private val context: Context
) : ToggleLikePostRepository {

    override suspend fun toggleLikePost(postid: Int): Flow<Resource<ToggleLikePostResponse>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.toggleLikePost(postid).body()?.let{ result ->
                    if(result.resultCode == 200) {
                        emit(Resource.Success(result.toToggleLikePostResponse(result.isLiked,result.isTokenValid)))
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