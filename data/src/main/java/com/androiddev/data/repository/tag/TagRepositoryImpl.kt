package com.androiddev.data.repository.tag

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.tag.TagApi
import com.androiddev.data.remote.dto.toGetTagsResponse
import com.androiddev.data.remote.dto.toSearchTags
import com.androiddev.data.remote.dto.toTags
import com.androiddev.domain.model.GetTagsResponse
import com.androiddev.domain.model.SearchTagResponse
import com.androiddev.domain.model.SearchedTags
import com.androiddev.domain.model.Tags
import com.androiddev.domain.repository.tag.TagRepository
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
    override suspend fun getTags(): Flow<Resource<Tags>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.getTags().body()?.let { result ->
                    if(!result.isTokenValid) {

                    } else if(result.resultCode == 200 && result.data!= null) {
                        val getTagsData = result.data.toTags(
                            favoriteTags = result.data.favoriteTags,
                            popularTags = result.data.popularTags
                        )
                        emit(Resource.Success(getTagsData))
                    }
                    else {
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
    override suspend fun toggleFavoriteTag(tagId:Int): Flow<Resource<Tags>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.toggleFavoriteTag(tagId).body()?.let { result ->
                    if(!result.isTokenValid) {

                    } else if(result.resultCode == 200 && result.data!= null) {
                        val getTagsData = result.data.toTags(
                            favoriteTags = result.data.favoriteTags,
                            popularTags = result.data.popularTags
                        )
                        emit(Resource.Success(getTagsData))
                    }
                    else {
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

    override suspend fun searchTag(tag:String): Flow<Resource<SearchedTags>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.searchTag(tag).body()?.let { result ->
                    if(!result.isTokenValid) {

                    } else if(result.resultCode == 200 && result.data!= null) {
                        val getTagsData = result.data.toSearchTags(
                            searchedTags = result.data.searchedTags
                        )
                        emit(Resource.Success(getTagsData))
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