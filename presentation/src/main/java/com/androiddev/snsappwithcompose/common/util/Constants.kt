package com.androiddev.snsappwithcompose.common.util

object Constants {
    const val AUTH_LIMITEDTIME = 180
    const val PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$"

    const val MEDIA_TYPE_IMAGE = "IMAGE"
    const val MEDIA_TYPE_VIDEO = "VIDEO"
    const val MEDIA_TYPE_AUDIO = "AUDIO"
    const val SEEK_TIME = 5_000L
}