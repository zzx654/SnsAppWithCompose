package com.androiddev.data.remote

data class BaseApiResponse<T>(
    val isTokenValid: Boolean,
    val resultCode: Int,
    val data: T?
)