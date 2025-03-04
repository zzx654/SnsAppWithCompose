package com.androiddev.data.remote.api

import com.androiddev.domain.model.SearchTagResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UploadPostApi {
    @FormUrlEncoded
    @POST("/searchTag")
    suspend fun searchTag(
        @Field("tag")tag: String
    ): Response<SearchTagResponse>
}