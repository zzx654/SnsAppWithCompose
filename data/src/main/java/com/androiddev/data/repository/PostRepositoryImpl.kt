package com.androiddev.data.repository

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.PostApi
import com.androiddev.domain.repository.PostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val context: Context,
    private val api:PostApi
):PostRepository {
    override suspend fun deletePost(postId: Int): Flow<Resource<Boolean>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.deletePost(postId).body()?.let{ result ->
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