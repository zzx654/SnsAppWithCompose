package com.androiddev.data.remote

open class BaseApiResponse<T>(
    val isTokenValid:Boolean?,
    val resultCode: Int,
    val data: T?
)