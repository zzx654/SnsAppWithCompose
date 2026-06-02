package com.androiddev.data.remote.api.uploadpost

import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.dto.GetPostsDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadPostApi {
    @Multipart
    @POST("post/uploadPost")
    suspend fun uploadPost(
        @Part ("anonymousNick") anonymousNick: RequestBody?,
        @Part ("tags") tags: RequestBody?,
        @Part media: List<MultipartBody.Part>?,
        @Part ("mediaTypes")mediaTypes: List<@JvmSuppressWildcards RequestBody>?,
        @Part ("voteoptions") voteOptions: RequestBody?,
        @Part ("text") text: RequestBody,
        @Part latitude: MultipartBody.Part?,
        @Part longitude: MultipartBody.Part?
    ): Response<BaseApiResponse<Unit>>

    @Multipart
    @POST("post/editPost")
    suspend fun editPost(
        @Part postid: MultipartBody.Part,
        @Part latitude: MultipartBody.Part?,
        @Part longitude: MultipartBody.Part?,
        @Part ("anonymousNick") anonymousNick: RequestBody?,
        @Part ("deletedVisualMedia") deletedVisualMedia: RequestBody?,
        @Part ("tags") tags: RequestBody?,
        @Part media: List<MultipartBody.Part>?,
        @Part ("mediaTypes")mediaTypes: List<@JvmSuppressWildcards RequestBody>?,
        @Part ("deletedAudio") deletedAudio: RequestBody?,
        @Part ("text") text: RequestBody
    ): Response<BaseApiResponse<GetPostsDto>>

}