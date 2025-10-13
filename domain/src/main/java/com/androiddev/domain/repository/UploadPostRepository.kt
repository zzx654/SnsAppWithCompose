package com.androiddev.domain.repository

import com.androiddev.domain.model.SearchTagResponse
import com.androiddev.domain.model.TagInfo
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UploadPostRepository {

    suspend fun searchTag(tag: String): Flow<Resource<SearchTagResponse>>

    suspend fun uploadPost(
        anonymousNick: RequestBody?,
        tags: RequestBody?,
        image:List<MultipartBody.Part>?,
        text: RequestBody,
        latitude: MultipartBody.Part?,
        longitude: MultipartBody.Part?
    ): Flow<Resource<Unit>>

}