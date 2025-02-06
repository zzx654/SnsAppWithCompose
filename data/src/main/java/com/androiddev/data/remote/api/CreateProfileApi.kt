package com.androiddev.data.remote.api

import com.androiddev.domain.model.CreateProfileResponse
import com.androiddev.domain.model.ValidationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CreateProfileApi {
    @Multipart
    @POST("/createProfile")
    suspend fun createProfile(
        @Part imageFile: MultipartBody.Part?,
        @Part("nickname")nickname: RequestBody,
        @Part("birth")birth: Int,
        @Part("gender")gender: RequestBody
    ): Response<CreateProfileResponse>
    @FormUrlEncoded
    @POST("/checkNickname")
    suspend fun checkNickname(
        @Field("nickname")nickname: String
    ): Response<ValidationResponse>
}