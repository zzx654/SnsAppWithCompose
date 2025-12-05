package com.androiddev.data.remote.api.fcm

import com.androiddev.data.remote.BaseApiResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FcmApi {
    @FormUrlEncoded
    @POST("/fcmToken")
    suspend fun sendFcmToken(
        @Field("fcmtoken") fcmtoken: String
    ): Response<BaseApiResponse<Unit>>
}