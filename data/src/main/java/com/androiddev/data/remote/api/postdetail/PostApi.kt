package com.androiddev.data.remote.api.postdetail

import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.dto.GetPostsDto
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
    @FormUrlEncoded
    @POST("/getPost")
    suspend fun getPost(
        @Field("postid")postid: Int,
        @Field("latitude")latitude:Double?,
        @Field("longitude")longitude:Double?
    ):Response<BaseApiResponse<GetPostsDto>>
}