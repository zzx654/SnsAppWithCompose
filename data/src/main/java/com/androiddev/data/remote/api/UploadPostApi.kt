package com.androiddev.data.remote.api

import com.androiddev.data.remote.dto.SearchTagResponseDto
import com.androiddev.domain.model.ResultCodeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadPostApi {
    @FormUrlEncoded
    @POST("/searchTag")
    suspend fun searchTag(
        @Field("tag")tag: String
    ): Response<SearchTagResponseDto>
    @Multipart
    @POST("/uploadPost")
    suspend fun uploadPost(
        @Part ("anonymousNick") anonymousNick: RequestBody?,
        @Part ("tags") tags: RequestBody?,
        @Part imageFiles: List<MultipartBody.Part>?,
        @Part audioFile: MultipartBody.Part?,
        @Part ("voteoptions") voteOptions: RequestBody?,
        @Part ("text") text: RequestBody,
        @Part latitude: MultipartBody.Part?,
        @Part longitude: MultipartBody.Part?
    ): Response<ResultCodeResponse>

}