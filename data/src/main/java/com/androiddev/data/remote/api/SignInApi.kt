package com.androiddev.data.remote.api

import com.androiddev.data.remote.dto.SigninResponseDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SignInApi {
    @FormUrlEncoded
    @POST("/socialSign")
    suspend fun socialSignIn(
        @Field("platform")platform: String,
        @Field("account")account: String
    ): Response<SigninResponseDto>
}