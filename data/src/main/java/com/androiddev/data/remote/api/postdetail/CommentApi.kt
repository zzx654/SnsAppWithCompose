package com.androiddev.data.remote.api.postdetail

import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.dto.CommentsDto
import com.androiddev.data.remote.dto.NotificationCommentDto
import com.androiddev.data.remote.dto.ToggleLikeDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CommentApi {
    @FormUrlEncoded
    @POST("/postReply")
    suspend fun postReply(
        @Field("postid")postid: Int,
        @Field("ref")ref: Int,
        @Field("text")text: String,
        @Field("anonymousNick")anonymousNick : String?
    ): Response<BaseApiResponse<CommentsDto>>
    @FormUrlEncoded
    @POST("/getReplies")
    suspend fun getReplies(
        @Field("ref")ref: Int,
        @Field("commentid")commentid: Int?,
        @Field("commentdate")commentdate : String?
    ): Response<BaseApiResponse<CommentsDto>>
    @FormUrlEncoded
    @POST("/getSelectedComment")
    suspend fun getSelectedComment(
        @Field("postid")postid: Int,
        @Field("commentid")commentid: Int?,
    ): Response<BaseApiResponse<CommentsDto>>
    @FormUrlEncoded
    @POST("/getComments")
    suspend fun getComments(
        @Field("postid")postid: Int,
        @Field("commentid")commentid: Int?,
        @Field("commentdate")commentdate : String?
    ): Response<BaseApiResponse<CommentsDto>>
    @FormUrlEncoded
    @POST("/getPopularComments")
    suspend fun getPopularComments(
        @Field("postid")postid: Int,
        @Field("commentid")commentid: Int?,
        @Field("score")score : Int
    ): Response<BaseApiResponse<CommentsDto>>
    @FormUrlEncoded
    @POST("/postComment")
    suspend fun postComments(
        @Field("postid")postid: Int,
        @Field("text")text: String,
        @Field("anonymousNick")anonymousNick : String?
    ): Response<BaseApiResponse<CommentsDto>>

    @FormUrlEncoded
    @POST("/toggleLikeComment")
    suspend fun toggleLikeComment(
        @Field("commentid")commentid: Int,
    ): Response<BaseApiResponse<ToggleLikeDto>>

    @FormUrlEncoded
    @POST("/getNotificationComment")
    suspend fun getNotificationComment(
        @Field("commentid")commentid: Int,
    ): Response<BaseApiResponse<NotificationCommentDto>>
}