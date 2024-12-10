package com.androiddev.data.remote.api

import com.androiddev.data.remote.dto.SigninResponseDto
import com.androiddev.domain.model.AuthCodeResponse
import com.androiddev.domain.model.RequestCodeResponse
import com.androiddev.domain.model.ResultCodeResponse
import com.androiddev.domain.model.SocialSignupResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SignUpApi {
    @FormUrlEncoded
    @POST("/requestEmailAuthCode")
    suspend fun requestAuthCode(
        @Field("email")email: String
    ): Response<RequestCodeResponse>
    @FormUrlEncoded
    @POST("/emailSignUp")
    suspend fun emailSignUp(
        @Field("account")account: String,
        @Field("password")password: String,
        @Field("phonenumber")phonenumber: String,
        @Field("authCode")authCode: String
    ): Response<AuthCodeResponse>
    @FormUrlEncoded
    @POST("/socialSignUp")
    suspend fun socialSignUp(
        @Field("platform")platform: String,
        @Field("account")account: String,
        @Field("phonenumber")phonenumber: String
    ): Response<SocialSignupResponse>
}