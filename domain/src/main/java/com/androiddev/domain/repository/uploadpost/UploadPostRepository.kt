package com.androiddev.domain.repository.uploadpost

import com.androiddev.domain.model.Posts
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UploadPostRepository {


    suspend fun uploadPost(
        anonymousNick: RequestBody?,
        tags: RequestBody?,
        media:List<MultipartBody.Part>?,
        mediaTypes:List<RequestBody>?,
        voteOptions:RequestBody?,
        text: RequestBody,
        latitude: MultipartBody.Part?,
        longitude: MultipartBody.Part?
    ): Flow<Resource<Unit>>
    suspend fun editPost(
        postid: MultipartBody.Part,
        latitude: MultipartBody.Part?,
        longitude: MultipartBody.Part?,
        anonymousNick: RequestBody?,
        tags: RequestBody?,
        media:List<MultipartBody.Part>?,
        mediaTypes:List<RequestBody>?,
        deletedVisualMedia:RequestBody?,
        deletedAudio:RequestBody?,
        text: RequestBody
    ): Flow<Resource<Posts>>

}