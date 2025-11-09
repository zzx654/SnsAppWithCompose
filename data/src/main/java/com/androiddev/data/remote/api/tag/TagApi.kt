package com.androiddev.data.remote.api.tag

import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.dto.GetTagsResponseDto
import com.androiddev.data.remote.dto.SearchTagResponseDto
import com.androiddev.data.remote.dto.SearchedTagsDto
import com.androiddev.data.remote.dto.TagsDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TagApi {
    @POST("/getTags")
    suspend fun getTags(): Response<BaseApiResponse<TagsDto>>

    @FormUrlEncoded
    @POST("/searchTag")
    suspend fun searchTag(
        @Field("tag")tag: String,
    ): Response<BaseApiResponse<SearchedTagsDto>>

    @FormUrlEncoded
    @POST("/ToggleFavoriteTag")
    suspend fun toggleFavoriteTag(
        @Field("tagid")tagid: Int
    ): Response<GetTagsResponseDto>
}