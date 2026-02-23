package com.androiddev.data.remote.api.signin

import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.dto.SigninResultDto
import com.androiddev.data.remote.dto.SigninWithTokenResultDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SignInApi {
    @FormUrlEncoded
    @POST("auth/emailSignIn")
    suspend fun emailSignIn(
        @Field("account")account: String,
        @Field("password")password: String,
        @Field("fcmtoken")fcmtoken: String
    ): Response<BaseApiResponse<SigninResultDto>>
    @FormUrlEncoded
    @POST("auth/socialSign")
    suspend fun socialSignIn(
        @Field("platform")platform: String,
        @Field("account")account: String,
        @Field("fcmtoken")fcmtoken: String
    ): Response<BaseApiResponse<SigninResultDto>>

    @POST("auth/signInWithToken")
    suspend fun signInWithToken(): Response<BaseApiResponse<SigninWithTokenResultDto>>

}