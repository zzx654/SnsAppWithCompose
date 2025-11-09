package com.androiddev.data.remote.api.tag

import com.androiddev.data.remote.dto.GetTagsResponseDto
import com.androiddev.data.remote.dto.SearchTagResponseDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TagApi {
    @POST("/getTags")
    suspend fun getTags(): Response<GetTagsResponseDto>

    @FormUrlEncoded
    @POST("/searchTag")
    suspend fun searchTag(
        @Field("tag")tag: String,
    ): Response<SearchTagResponseDto>

    @FormUrlEncoded
    @POST("/ToggleFavoriteTag")
    suspend fun toggleFavoriteTag(
        @Field("tagid")tagid: Int
    ): Response<GetTagsResponseDto>
}