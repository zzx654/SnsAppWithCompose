package com.androiddev.data.remote.api.signup

import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.dto.AuthCodeResultDto
import com.androiddev.data.remote.dto.ValidationResultDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthPhoneApi {
    @FormUrlEncoded
    @POST("/requestPhoneAuthCode")
    suspend fun requestAuthCode(
        @Field("phoneNumber")phoneNumber: String
    ): Response<BaseApiResponse<ValidationResultDto>>
    @FormUrlEncoded
    @POST("/authenticateCode")
    suspend fun authenticateCode(
        @Field("phoneNumber")phoneNumber: String,
        @Field("authCode")authCode: String
    ): Response<BaseApiResponse<AuthCodeResultDto>>
}