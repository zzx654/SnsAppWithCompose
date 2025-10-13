package com.androiddev.data.repository

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.TagApi
import com.androiddev.data.remote.dto.toGetPostsResponse
import com.androiddev.data.remote.dto.toGetTagsResponse
import com.androiddev.domain.model.GetTagsResponse
import com.androiddev.domain.model.SearchTagResponse
import com.androiddev.domain.repository.TagRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val api: TagApi,
    private val context: Context
) : TagRepository {
    override suspend fun getTags(): Flow<Resource<GetTagsResponse>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.getTags().body()?.let { result ->
                    if(result.resultCode == 200) {
                        val getTagsResult = result.toGetTagsResponse(
                            isTokenValid = result.isTokenValid,
                            favoriteTags = result.favoriteTags,
                            popularTags = result.popularTags
                        )
                        emit(Resource.Success(getTagsResult))
                    } else {
                        emit(Resource.Error(getString(context,R.string.server_error)))
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

    override suspend fun searchTag(tag:String): Flow<Resource<SearchTagResponse>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.searchTag(tag).body()?.let { result ->
                    if(result.resultCode == 200) {
                        val getTagsResult = result.toGetTagsResponse(
                            isTokenValid = result.isTokenValid,
                            searchedTags = result.searchedTags
                        )
                        emit(Resource.Success(getTagsResult))
                    } else {
                        emit(Resource.Error(getString(context,R.string.server_error)))
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

}