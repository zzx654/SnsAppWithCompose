package com.androiddev.data.repository.tag

import android.content.Context
import com.androiddev.data.remote.api.tag.TagApi
import com.androiddev.data.remote.dto.toSearchTags
import com.androiddev.data.remote.dto.toTags
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.SearchedTags
import com.androiddev.domain.model.Tags
import com.androiddev.domain.repository.tag.TagRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val api: TagApi,
    private val context: Context
) : TagRepository {
    override suspend fun getTags(): Flow<Resource<Tags>> = safeApiCall(
        context = context,
        apiCall = { api.getTags() },
        mapToResource = { it.toTags()
        }
    )
    override suspend fun toggleFavoriteTag(tagId:Int): Flow<Resource<Tags>> =
        safeApiCall(
            context = context,
            apiCall = { api.toggleFavoriteTag(tagId) },
            mapToResource = {
                it.toTags(
                )
            }
        )
    override suspend fun searchTag(tag:String): Flow<Resource<SearchedTags>> =
        safeApiCall(
            context = context,
            apiCall = { api.searchTag(tag) },
            mapToResource = {
                it.toSearchTags(
                )
            }
        )

}