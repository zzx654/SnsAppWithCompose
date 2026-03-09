package com.androiddev.data.remote.api.postdetail


import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.dto.ToggleLikeDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ToggleLikePostApi {

    @FormUrlEncoded
    @POST("postdetail/toggleLikePost")
    suspend fun toggleLikePost(
        @Field("postid")postid: Int,
    ): Response<BaseApiResponse<ToggleLikeDto>>

}