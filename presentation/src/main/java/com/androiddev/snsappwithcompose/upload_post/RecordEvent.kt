package com.androiddev.snsappwithcompose.upload_post

import android.net.Uri

sealed class RecordEvent {
    data object OnAddRecordClick: RecordEvent()
    data object RecordPlayBack: RecordEvent()
    data object OnCancelClick: RecordEvent()
    data class AddTag(val tagIndex: Int) : UploadPostEvent()

}