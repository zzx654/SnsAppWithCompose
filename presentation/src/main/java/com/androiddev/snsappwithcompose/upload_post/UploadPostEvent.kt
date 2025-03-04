package com.androiddev.snsappwithcompose.upload_post

sealed class UploadPostEvent {
    data class TypeTag(val tag: String) : UploadPostEvent()
}