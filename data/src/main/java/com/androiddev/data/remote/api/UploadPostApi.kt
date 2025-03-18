package com.androiddev.data.remote.api

import com.androiddev.domain.model.ResultCodeResponse
import com.androiddev.domain.model.SearchTagResponse
import okhttp3.MultipartBody
import okhttp3.Request
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
    ): Response<SearchTagResponse>
    @Multipart
    @POST("/uploadPost")
    suspend fun uploadPost(
        @Part ("tags") tags: RequestBody?,
        @Part imageFiles: List<MultipartBody.Part>?,
        @Part ("text") text: RequestBody
    ): Response<ResultCodeResponse>

}