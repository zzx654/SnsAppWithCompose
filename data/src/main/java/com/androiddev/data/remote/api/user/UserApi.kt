package com.androiddev.data.remote.api.user

import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.dto.MediaPostsDto
import com.androiddev.data.remote.dto.ToggleFollowDto
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
        @Field("lastuserid")lastuserid: Long?
    ): Response<BaseApiResponse<UsersDto>>

    @FormUrlEncoded
    @POST("user/toggleFollowUser")
    suspend fun toggleFollowUser(
        @Field("userid")userid: Long,
    ): Response<BaseApiResponse<ToggleFollowDto>>

    @FormUrlEncoded
    @POST("user/getUserInfo")
    suspend fun getUserInfo(
        @Field("userid")userid: Long,
    ): Response<BaseApiResponse<UsersDto>>

    @FormUrlEncoded
    @POST("user/getMedia")
    suspend fun getMedia(
        @Field("userid")userid: Long,
        @Field("type")type: String,
        @Field("mediaid")mediaid: Long?,
        @Field("latitude")latitude: Double?,
        @Field("longitude")longitude: Double?,
    ): Response<BaseApiResponse<MediaPostsDto>>

}