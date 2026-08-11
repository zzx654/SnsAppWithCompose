package com.androiddev.domain.util



inline fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success && data != null) {
        action(data)
    }
    return this
}

inline fun <T> Resource<T>.onError(action: (message: String?) -> Unit): Resource<T> {
    if (this is Resource.Error) {
        action(message)
    }
    return this
}


inline fun <T> Resource<T>.onLoading(action: () -> Unit): Resource<T> {
    if (this is Resource.Loading) {
        action()
    }
    return this
}