package com.androiddev.data.remote.dto

import com.androiddev.data.util.Constants.SUCCESS
import com.androiddev.domain.model.DeleteReason
import com.androiddev.domain.model.NotificationActionResult
import com.androiddev.domain.model.ReadNotificationResult

data class ReadNotificationDto(
    val status:String,
    val reason:String?,
    val unreadCount:Int?
)
fun ReadNotificationDto.toReadNotificationResult(
): ReadNotificationResult {
    return ReadNotificationResult(
        notificationActionResult = if(status == SUCCESS &&reason==null) NotificationActionResult.Navigate else NotificationActionResult.TargetDeleted(DeleteReason.valueOf(reason!!)),
        unreadCount = unreadCount
    )

}