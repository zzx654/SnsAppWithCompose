package com.androiddev.data.remote.api.user

import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.dto.ToggleFollowDto
import com.androiddev.data.remote.dto.UserDto
import com.androiddev.data.remote.dto.UsersDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserApi {
    @FormUrlEncoded
    @POST("user/getSearchedUsers")
    suspend fun getSearchedUsers(
        @Field("nickname")nickname: String,
        @Field("lastuserid")lastuserid:Int?
    ): Response<BaseApiResponse<UsersDto>>

    @FormUrlEncoded
    @POST("user/toggleFollowUser")
    suspend fun toggleFollowUser(
        @Field("userid")userid: Int,
    ): Response<BaseApiResponse<ToggleFollowDto>>

    @FormUrlEncoded
    @POST("user/getUserInfo")
    suspend fun getUserInfo(
        @Field("userid")userid: Int,
    ): Response<BaseApiResponse<UsersDto>>


}