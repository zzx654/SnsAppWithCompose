package com.androiddev.data.remote.api

import com.androiddev.data.remote.dto.GetCommentsResponseDto
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
    @POST("/postComment")
    suspend fun postComments(
        @Field("postid")postid: Int,
        @Field("text")text: String,
        @Field("anonymousNick")anonymousNick : String?
    ): Response<GetCommentsResponseDto>
}