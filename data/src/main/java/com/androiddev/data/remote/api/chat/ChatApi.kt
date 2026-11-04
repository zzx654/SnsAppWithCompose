package com.androiddev.data.remote.api.chat

import com.androiddev.data.remote.BaseApiResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ChatApi {

    @FormUrlEncoded
    @POST("chat/createChatRequest")
    suspend fun createChatRequest(
        @Field("targetuserid") targetuserid: Long,
        @Field("postid") postid: Long?,
        @Field("commentid") commentid: Long?
    ): Response<BaseApiResponse<Unit>>
}