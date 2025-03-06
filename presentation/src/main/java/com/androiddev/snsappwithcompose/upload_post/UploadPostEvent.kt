package com.androiddev.snsappwithcompose.upload_post

sealed class UploadPostEvent {
    data class TypeTag(val tag: String) : UploadPostEvent()
    data class AddTag(val tagIndex: Int) : UploadPostEvent()
    data class DeleteTag(val tag: String) : UploadPostEvent()
    data class TypeContent(val text: String) : UploadPostEvent()
    data class ToggleCheckBox(val isChecked: Boolean) : UploadPostEvent()
}