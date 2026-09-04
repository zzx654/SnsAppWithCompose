package com.androiddev.snsappwithcompose.feature.upload_post.record

import com.androiddev.data.util.Constants.MAX_DURATION_MILLIS
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.util.UiText

data class RecordUIState(
    val state: RecordState = RecordState.IDLE,
    val progress: Float = 0f,
    val elapsedMillis: Long = 0L,
    val formattedTime: UiText = UiText.StringResource(R.string.default_formatted_time),
    val maxDurationMillis: Long = MAX_DURATION_MILLIS,
    val recordedFilePath: String? = null,
    val recorded: Boolean = false,
    val bottomRecordDialogState: BottomRecordState = BottomRecordState()
) {
    val buttonAction: RecordButtonAction
        get() = when (state) {
            RecordState.IDLE -> RecordButtonAction.START_RECORDING
            RecordState.RECORDING -> RecordButtonAction.STOP_RECORDING
            RecordState.RECORDED -> RecordButtonAction.START_PLAYBACK
            RecordState.PLAYING -> RecordButtonAction.STOP_PLAYBACK
        }
}
enum class RecordButtonAction {
    START_RECORDING,
    STOP_RECORDING,
    START_PLAYBACK,
    STOP_PLAYBACK
}