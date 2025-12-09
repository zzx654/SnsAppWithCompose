package com.androiddev.data.remote.api.postdetail

import com.androiddev.data.remote.BaseApiResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface PostApi {
    @FormUrlEncoded
    @POST("/deletePost")
    suspend fun deletePost(
        @Field("postid")postid: Int
    ): Response<BaseApiResponse<Unit>>
}