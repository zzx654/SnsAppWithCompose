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
    @POST("comment/postReply")
    suspend fun postReply(
        @Field("postid")postid: Int,
        @Field("ref")ref: Int,
        @Field("text")text: String,
        @Field("anonymousNick")anonymousNick : String?
    ): Response<BaseApiResponse<CommentsDto>>
    @FormUrlEncoded
    @POST("comment/getReplies")
    suspend fun getReplies(
        @Field("ref")ref: Int,
        @Field("commentid")commentid: Int?,
        @Field("commentdate")commentdate : String?
    ): Response<BaseApiResponse<CommentsDto>>
    @FormUrlEncoded
    @POST("comment/getSelectedComment")
    suspend fun getSelectedComment(
        @Field("postid")postid: Int,
        @Field("commentid")commentid: Int?,
    ): Response<BaseApiResponse<CommentsDto>>
    @FormUrlEncoded
    @POST("comment/getComments")
    suspend fun getComments(
        @Field("postid")postid: Int,
        @Field("commentid")commentid: Int?,
        @Field("commentdate")commentdate : String?
    ): Response<BaseApiResponse<CommentsDto>>
    @FormUrlEncoded
    @POST("comment/getPopularComments")
    suspend fun getPopularComments(
        @Field("postid")postid: Int,
        @Field("commentid")commentid: Int?,
        @Field("score")score : Int
    ): Response<BaseApiResponse<CommentsDto>>
    @FormUrlEncoded
    @POST("comment/postComment")
    suspend fun postComments(
        @Field("postid")postid: Int,
        @Field("text")text: String,
        @Field("anonymousNick")anonymousNick : String?
    ): Response<BaseApiResponse<CommentsDto>>

    @FormUrlEncoded
    @POST("comment/toggleLikeComment")
    suspend fun toggleLikeComment(
        @Field("commentid")commentid: Int,
    ): Response<BaseApiResponse<ToggleLikeDto>>

    @FormUrlEncoded
    @POST("comment/getNotificationComment")
    suspend fun getNotificationComment(
        @Field("commentid")commentid: Int,
    ): Response<BaseApiResponse<NotificationCommentDto>>
}