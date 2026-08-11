package com.androiddev.snsappwithcompose.common.util

import com.androiddev.domain.util.DataError
import com.androiddev.snsappwithcompose.R

fun DataError.toUiText(): UiText {
    return when (this) {
        DataError.Network.SERVER_ERROR -> UiText.StringResource(com.androiddev.data.R.string.server_error)
        DataError.Network.CONNECTION_ERROR -> UiText.StringResource(R.string.connection_error)
        DataError.Network.UNEXPECTED_ERROR -> UiText.StringResource(com.androiddev.data.R.string.unexpected_error)
        DataError.Network.TOKEN_EXPIRED -> UiText.StringResource(com.androiddev.data.R.string.token_expired)
    }
}