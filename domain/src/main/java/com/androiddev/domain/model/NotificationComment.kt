package com.androiddev.domain.model

data class NotificationComment (
    val comment:Comment,
    val reply:Comment?
)