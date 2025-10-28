package com.androiddev.snsappwithcompose.upload_post

import android.net.Uri

sealed class UploadPostEvent {
    data class TypeTag(val tag: String) : UploadPostEvent()
    data class AddTag(val tagIndex: Int) : UploadPostEvent()
    data class DeleteTag(val tag: String) : UploadPostEvent()
    data class TypeContent(val text: String) : UploadPostEvent()
    data class ToggleCheckBox(val isChecked: Boolean) : UploadPostEvent()
    data class AddImages(val images: List<Uri>) : UploadPostEvent()
    data class DeleteImage(val image: EditableImage) : UploadPostEvent()
    data class SetLocationOnOff(val onOff: Boolean): UploadPostEvent()
    data class ToggleLocationOnOff(val onOff: Boolean): UploadPostEvent()
    data class UploadPost(val lat:Double? = null, val long:Double? = null, val audioFilePath:String? = null,val voteOptions:List<String>): UploadPostEvent()
}