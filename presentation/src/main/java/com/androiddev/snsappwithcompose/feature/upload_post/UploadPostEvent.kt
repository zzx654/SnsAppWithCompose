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
    data class AddMedia(val items:List<MediaItem>): UploadPostEvent()
    data class DeleteMedia(val media: MediaItem): UploadPostEvent()
    data object ToggleLocationOnOff: UploadPostEvent()
    data class UploadPost(val audioFilePath:String? = null,val deletedAudio:String? = null, val voteOptions:List<String>): UploadPostEvent()
}