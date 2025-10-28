package com.androiddev.snsappwithcompose.upload_post.record

import com.androiddev.snsappwithcompose.upload_post.PostMode

sealed class RecordEvent {
    data object OnAddRecordClick: RecordEvent()
    data object RecordPlayBack: RecordEvent()
    data object OnCancelClick: RecordEvent()
    data object SaveRecording: RecordEvent()

}