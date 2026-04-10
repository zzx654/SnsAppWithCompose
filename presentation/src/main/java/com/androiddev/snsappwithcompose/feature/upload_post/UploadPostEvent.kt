package com.androiddev.snsappwithcompose.feature.upload_post

import android.net.Uri
import com.androiddev.snsappwithcompose.feature.upload_post.component.EditableImage
import com.androiddev.snsappwithcompose.feature.upload_post.component.MediaItem

sealed class UploadPostEvent {
    data class TypeTag(val tag: String) : UploadPostEvent()
    data class AddTag(val tagIndex: Int) : UploadPostEvent()
    data class DeleteTag(val tag: String) : UploadPostEvent()
    data class TypeContent(val text: String) : UploadPostEvent()
    data class ToggleCheckBox(val isChecked: Boolean) : UploadPostEvent()
    data class AddImages(val images: List<Uri>) : UploadPostEvent()
    data class AddMedia(val uris: List<Uri>): UploadPostEvent()
    data class DeleteMedia(val media: MediaItem): UploadPostEvent()
    data class DeleteImage(val image: EditableImage) : UploadPostEvent()
    data class SetLocationOnOff(val onOff: Boolean): UploadPostEvent()
    data class ToggleLocationOnOff(val onOff: Boolean): UploadPostEvent()
    data class UploadPost(val lat:Double? = null, val long:Double? = null, val audioFilePath:String? = null,val deleteAudio:String? = null, val voteOptions:List<String>): UploadPostEvent()
}