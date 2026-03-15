package com.androiddev.data.remote.api.notification

import com.androiddev.data.remote.BaseApiResponse
import com.androiddev.data.remote.dto.NotificationsDto
import com.androiddev.data.remote.dto.ReadNotificationDto
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Field

interface NotificationApi {
    @FormUrlEncoded
    @POST("notification/getNotifications")
    suspend fun getNotifications(
        @Field("notificationid")notificationid: Long?,
        @Field("notificationdate")notificationdate : String?
    ):Response<BaseApiResponse<NotificationsDto>>

    @POST("notification/readAllNotifications")
    suspend fun readAllNotifications():Response<BaseApiResponse<NotificationsDto>>

    @FormUrlEncoded
    @POST("notification/readNotification")
    suspend fun readNotification(
        @Field("notificationid")notificationid: Long?,
    ):Response<BaseApiResponse<ReadNotificationDto>>

    @POST("notification/deleteNotifications")
    suspend fun deleteNotifications():Response<BaseApiResponse<NotificationsDto>>
}