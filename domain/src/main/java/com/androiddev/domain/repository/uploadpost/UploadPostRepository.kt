package com.androiddev.domain.repository.uploadpost

import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UploadPostRepository {


    suspend fun uploadPost(
        anonymousNick: RequestBody?,
        tags: RequestBody?,
        image:List<MultipartBody.Part>?,
        audio:MultipartBody.Part?,
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
        deleteImages: RequestBody?,
        tags: RequestBody?,
        images:List<MultipartBody.Part>?,
        audio:MultipartBody.Part?,
        deleteAudio:RequestBody?,
        text: RequestBody
    ): Flow<Resource<GetPostsResponse>>

}