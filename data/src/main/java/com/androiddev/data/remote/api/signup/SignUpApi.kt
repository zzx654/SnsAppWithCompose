package com.androiddev.data.remote.api.signup

import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.dto.AuthCodeResultDto
import com.androiddev.data.remote.dto.TokenResultDto
import com.androiddev.data.remote.dto.ValidationResultDto
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
    ): Response<BaseApiResponse<ValidationResultDto>>
    @FormUrlEncoded
    @POST("/emailSignUp")
    suspend fun emailSignUp(
        @Field("account")account: String,
        @Field("password")password: String,
        @Field("phonenumber")phonenumber: String,
        @Field("authCode")authCode: String
    ): Response<BaseApiResponse<AuthCodeResultDto>>
    @FormUrlEncoded
    @POST("/socialSignUp")
    suspend fun socialSignUp(
        @Field("platform")platform: String,
        @Field("account")account: String,
        @Field("phonenumber")phonenumber: String
    ): Response<BaseApiResponse<TokenResultDto>>

}