package com.androiddev.domain.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val error: DataError? = null // 👈 새로 추가! (기본값 null)
) {
    class Success<T>(data: T? = null) : Resource<T>(data)

    class Error<T>(
        message: String? = null,
        data: T? = null,
        error: DataError? = null
    ) : Resource<T>(data = data, message = message, error = error) {

        constructor(error: DataError, data: T? = null) : this(message = null, data = data, error = error)
    }

    class Loading<T>(data: T? = null) : Resource<T>(data)
    class TokenExpired<T> : Resource<T>()
}