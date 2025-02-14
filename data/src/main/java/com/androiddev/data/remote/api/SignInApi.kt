package com.androiddev.data.remote.api

import com.androiddev.data.remote.dto.SigninResponseDto
import com.androiddev.data.remote.dto.SigninWithTokenResponseDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SignInApi {
    @FormUrlEncoded
    @POST("/emailSignIn")
    suspend fun emailSignIn(
        @Field("account")account: String,
        @Field("password")password: String,
    ): Response<SigninResponseDto>
    @FormUrlEncoded
    @POST("/socialSign")
    suspend fun socialSignIn(
        @Field("platform")platform: String,
        @Field("account")account: String
    ): Response<SigninResponseDto>

    @POST("/signInWithToken")
    suspend fun signInWithToken(): Response<SigninWithTokenResponseDto>

}