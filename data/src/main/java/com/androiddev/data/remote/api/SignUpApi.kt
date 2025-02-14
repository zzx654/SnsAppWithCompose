package com.androiddev.data.remote.api

import com.androiddev.domain.model.AuthCodeResponse
import com.androiddev.domain.model.SocialSignupResponse
import com.androiddev.domain.model.ValidationResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SignUpApi {
    @FormUrlEncoded
    @POST("/requestEmailAuthCode")
    suspend fun requestAuthCode(
        @Field("email")email: String
    ): Response<ValidationResponse>
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