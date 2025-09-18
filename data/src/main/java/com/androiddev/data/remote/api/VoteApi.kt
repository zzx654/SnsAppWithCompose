package com.androiddev.data.remote.api

import com.androiddev.data.remote.dto.GetVoteResponseDto
import com.androiddev.domain.model.CancelVoteResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface VoteApi {
    @FormUrlEncoded
    @POST("/getVoteInfo")
    suspend fun getVoteInfo(
        @Field("postid")postid: Int
    ): Response<GetVoteResponseDto>

    @FormUrlEncoded
    @POST("/vote")
    suspend fun vote(
        @Field("postid")postid: Int,
        @Field("optionid")optionid: Int
    ): Response<GetVoteResponseDto>
    @FormUrlEncoded
    @POST("/cancelVote")
    suspend fun cancelVote(
        @Field("postid")postid: Int
    ): Response<CancelVoteResponse>

}