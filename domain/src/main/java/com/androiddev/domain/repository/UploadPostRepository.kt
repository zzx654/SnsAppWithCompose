package com.androiddev.domain.repository

import com.androiddev.domain.model.TagInfo
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface UploadPostRepository {

    suspend fun searchTag(tag: String): Flow<Resource<List<TagInfo>>>

}