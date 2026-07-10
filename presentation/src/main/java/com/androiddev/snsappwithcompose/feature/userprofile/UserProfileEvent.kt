package com.androiddev.snsappwithcompose.feature.userprofile

sealed class UserProfileEvent {
    data object OnClickImageItem : UserProfileEvent()

}