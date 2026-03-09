package com.androiddev.data.remote.api.postdetail

import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.dto.VoteInfoDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface VoteApi {
    @FormUrlEncoded
    @POST("postdetail/getVoteInfo")
    suspend fun getVoteInfo(
        @Field("postid")postid: Int
    ): Response<BaseApiResponse<VoteInfoDto>>

    @FormUrlEncoded
    @POST("postdetail/vote")
    suspend fun vote(
        @Field("postid")postid: Int,
        @Field("optionid")optionid: Int
    ): Response<BaseApiResponse<VoteInfoDto>>
    @FormUrlEncoded
    @POST("postdetail/cancelVote")
    suspend fun cancelVote(
        @Field("postid")postid: Int
    ): Response<BaseApiResponse<Unit>>

}