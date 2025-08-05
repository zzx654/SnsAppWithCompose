package com.androiddev.data.remote.api

import com.androiddev.data.remote.dto.GetCommentsResponseDto
import com.androiddev.data.remote.dto.ToggleLikeResponseDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CommentApi {
    @FormUrlEncoded
    @POST("/getComments")
    suspend fun getComments(
        @Field("postid")postid: Int,
        @Field("commentid")commentid: Int?,
        @Field("commentdate")commentdate : String?
    ): Response<GetCommentsResponseDto>
    @FormUrlEncoded
    @POST("/getPopularComments")
    suspend fun getPopularComments(
        @Field("postid")postid: Int,
        @Field("commentid")commentid: Int?,
        @Field("score")score : Int
    ): Response<GetCommentsResponseDto>
    @FormUrlEncoded
    @POST("/postComment")
    suspend fun postComments(
        @Field("postid")postid: Int,
        @Field("text")text: String,
        @Field("anonymousNick")anonymousNick : String?
    ): Response<GetCommentsResponseDto>

    @FormUrlEncoded
    @POST("/toggleLikeComment")
    suspend fun toggleLikeComment(
        @Field("commentid")commentid: Int,
    ): Response<ToggleLikeResponseDto>
}