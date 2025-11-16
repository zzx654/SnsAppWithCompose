package com.androiddev.data.remote.api.createprofile

import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.dto.ValidationResultDto
import com.androiddev.domain.model.CreateProfileResponse
import com.androiddev.domain.model.ValidationResponse
import com.androiddev.domain.model.ValidationResult
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
    ): Response<BaseApiResponse<Unit>>
    @FormUrlEncoded
    @POST("/checkNickname")
    suspend fun checkNickname(
        @Field("nickname")nickname: String
    ): Response<BaseApiResponse<ValidationResultDto>>
}