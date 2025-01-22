package com.androiddev.data.remote.api

import com.androiddev.data.remote.dto.UploadImageResponseDto
import com.androiddev.domain.model.ValidationResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CreateProfileApi {
    @Multipart
    @POST("/uploadimg")
    suspend fun uploadimg(
        @Part imageFile: MultipartBody.Part): Response<UploadImageResponseDto>
    @FormUrlEncoded
    @POST("/checkNickname")
    suspend fun checkNickname(
        @Field("nickname")nickname: String
    ): Response<ValidationResponse>
}