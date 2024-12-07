package com.androiddev.data.remote.api

import com.androiddev.domain.model.AuthCodeResponse
import com.androiddev.domain.model.ResultCodeResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthPhoneApi {
    @FormUrlEncoded
    @POST("/requestPhoneAuthCode")
    suspend fun requestAuthCode(
        @Field("phoneNumber")phoneNumber: String
    ): Response<ResultCodeResponse>
    @FormUrlEncoded
    @POST("/authenticateCode")
    suspend fun authenticateCode(
        @Field("phoneNumber")phoneNumber: String,
        @Field("authCode")authCode: String
    ): Response<AuthCodeResponse>
}