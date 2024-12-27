package com.androiddev.snsappwithcompose.createprofile

sealed class CreateProfileEvent {
    object uploadImage: CreateProfileEvent()
}