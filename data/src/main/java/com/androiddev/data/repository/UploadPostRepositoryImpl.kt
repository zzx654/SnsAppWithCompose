package com.androiddev.data.repository

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.UploadPostApi
import com.androiddev.domain.model.TagInfo
import com.androiddev.domain.repository.UploadPostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UploadPostRepositoryImpl @Inject constructor(
    private val api: UploadPostApi,
    private val context: Context
): UploadPostRepository {
    override suspend fun searchTag(tag: String): Flow<Resource<List<TagInfo>>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.searchTag(tag).body()?.let{ result ->
                    if(result.resultCode == 200) {
                        emit(Resource.Success(result.tags))
                    }
                    else
                        emit(Resource.Error(getString(context,R.string.server_error)))
                }
            } catch(e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: getString(context,R.string.unexpected_error)))

            } catch(e: IOException) {
                emit(Resource.Error(getString(context,R.string.connection_error)))
            }
        }
    }

}
