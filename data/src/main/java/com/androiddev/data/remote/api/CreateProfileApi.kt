package com.androiddev.data.remote.api

import com.androiddev.data.remote.dto.UploadImageResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CreateProfileApi {
    @Multipart
    @POST("/uploadimg")
    suspend fun uploadimg(
        @Part imageFile: MultipartBody.Part): Response<UploadImageResponseDto>
}