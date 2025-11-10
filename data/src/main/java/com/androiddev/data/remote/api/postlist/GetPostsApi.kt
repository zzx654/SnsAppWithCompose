package com.androiddev.data.remote.api.postlist

import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.dto.GetPostsDto
import com.androiddev.data.remote.dto.GetPostsResponseDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GetPostsApi {
    @FormUrlEncoded
    @POST("/getNewTagPosts")
    suspend fun getNewTagPosts(
        @Field("postid")postid: Int?,
        @Field("postdate")postdate:String?,
        @Field("tagid")tagid: Int,
        @Field("latitude")latitude:Double?,
        @Field("longitude")longitude:Double?
    ): Response<BaseApiResponse<GetPostsDto>>
    @FormUrlEncoded
    @POST("/getPopularTagPosts")
    suspend fun getPopularTagPosts(
        @Field("postid")postid: Int?,
        @Field("tagid")tagid: Int,
        @Field("score")score : Double?,
        @Field("latitude")latitude:Double?,
        @Field("longitude")longitude:Double?
    ): Response<BaseApiResponse<GetPostsDto>>
    @FormUrlEncoded
    @POST("/getNearPosts")
    suspend fun getNearPosts(
        @Field("postid")postid: Int?,
        @Field("postdate")postdate: String?,
        @Field("distancemax")distancemax : Int,
        @Field("latitude")latitude:Double,
        @Field("longitude")longitude:Double
    ): Response<BaseApiResponse<GetPostsDto>>

    @FormUrlEncoded
    @POST("/getNewPosts")
    suspend fun getNewPosts(
        @Field("postid")postid: Int?,
        @Field("postdate")postdate: String?,
        @Field("latitude")latitude:Double?,
        @Field("longitude")longitude:Double?
    ): Response<BaseApiResponse<GetPostsDto>>

    @FormUrlEncoded
    @POST("/getSelectedPost")
    suspend fun getSelectedPost(
        @Field("postid")postid: Int,
        @Field("latitude")latitude:Double?,
        @Field("longitude")longitude:Double?
    ):Response<BaseApiResponse<GetPostsDto>>


}