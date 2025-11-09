package com.androiddev.domain.repository.tag

import com.androiddev.domain.model.GetTagsResponse
import com.androiddev.domain.model.SearchTagResponse
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    suspend fun getTags(): Flow<Resource<GetTagsResponse>>
    suspend fun searchTag(tag:String): Flow<Resource<SearchTagResponse>>
    suspend fun toggleFavoriteTag(tagId:Int): Flow<Resource<GetTagsResponse>>
}