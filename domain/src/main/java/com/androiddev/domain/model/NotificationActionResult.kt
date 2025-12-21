package com.androiddev.domain.model

sealed class NotificationActionResult {
    data object Navigate: NotificationActionResult()

    data class TargetDeleted(
        val reason: DeleteReason
    ) : NotificationActionResult()
}
enum class DeleteReason {
    POST_DELETED,
    COMMENT_DELETED,
    REPLY_DELETED,
    NOTIFICATION_NOT_FOUND
}