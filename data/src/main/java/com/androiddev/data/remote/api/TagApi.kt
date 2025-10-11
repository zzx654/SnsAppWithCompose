package com.androiddev.data.remote.api

import com.androiddev.data.remote.dto.GetTagsResponseDto
import retrofit2.Response
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TagApi {
    @POST("/getTags")
    suspend fun getTags(): Response<GetTagsResponseDto>
}