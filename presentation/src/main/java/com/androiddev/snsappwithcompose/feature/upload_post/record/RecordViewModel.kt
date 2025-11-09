package com.androiddev.snsappwithcompose.feature.upload_post.record

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import com.androiddev.data.R
import com.androiddev.snsappwithcompose.service.record.RecordService
import com.androiddev.data.util.Constants.DEFAULT_ELAPSED_TIME
import com.androiddev.data.util.Constants.DEFAULT_PROGRESS
import com.androiddev.data.util.Constants.MAX_DURATION_MILLIS
import com.androiddev.snsappwithcompose.service.record.RecordIntentKeys.STATE
import com.androiddev.snsappwithcompose.service.record.RecordIntentKeys.PROGRESS
import com.androiddev.snsappwithcompose.service.record.RecordIntentKeys.ELAPSED
import com.androiddev.snsappwithcompose.service.record.RecordIntentKeys.FILE_PATH
import com.androiddev.snsappwithcompose.service.record.RecordIntentKeys.FORMATTED_TIME
import com.androiddev.snsappwithcompose.common.state.AlertDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class RecordViewModel @Inject constructor(
    private val context: Context
): ViewModel() {
    private val _recordingAlertDialogState: MutableState<AlertDialogState> = mutableStateOf(
        AlertDialogState()
    )
    val recordingAlertDialogState: State<AlertDialogState>
        get() = _recordingAlertDialogState
    private val _bottomRecordDialogState: MutableState<BottomRecordState> = mutableStateOf(
        BottomRecordState()
    )
    val bottomRecordDialogState: State<BottomRecordState>
        get() = _bottomRecordDialogState
    private val _uiState = MutableStateFlow(RecordUIState(formattedTime = getString(context,R.string.default_formatted_time)))
    val uiState: StateFlow<RecordUIState> = _uiState.asStateFlow()

    private var receiverRegistered = false

    var prevRemotePath: String? = null
        private set
    var deletedAudio: String? = null
        private set

    private val _recordedFilePath: MutableState<String?> = mutableStateOf(null)
    val recordedFilePath: State<String?>
        get() = _recordedFilePath
    private val _recorded:MutableState<Boolean> = mutableStateOf(false)
    val recorded:State<Boolean>
        get() = _recorded

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            intent ?: return
            when (intent.action) {
                RecordService.ACTION_UPDATE -> {
                    val stateStr = intent.getStringExtra(STATE) ?: return
                    val elapsed = intent.getLongExtra(ELAPSED, DEFAULT_ELAPSED_TIME)
                    val progress = intent.getFloatExtra(PROGRESS, DEFAULT_PROGRESS)
                    val formattedTime = intent.getStringExtra(FORMATTED_TIME)
                    val filePath = intent.getStringExtra(FILE_PATH)
                    val state = RecordState.valueOf(stateStr)


                    filePath?.let {
                        _recordedFilePath.value = it
                        _recorded.value = true
                    }
                    _uiState.update {
                        it.copy(
                            state = state,
                            elapsedMillis = elapsed,
                            formattedTime = formattedTime?:_uiState.value.formattedTime,
                            progress = progress
                        )
                    }
                }
            }
        }
    }

    init {
        registerReceiver()
    }

    fun initRecordState(remotePath:String) {
        _recorded.value = true
        prevRemotePath = remotePath
    }
    fun onEvent(event: RecordEvent) {
        when(event) {
            is RecordEvent.OnAddRecordClick -> {

                if(recorded.value)
                    showDeleteRecordingAlert()
                else
                    showDialog()
            }
            is RecordEvent.RecordPlayBack -> {
                when (_uiState.value.buttonAction) {
                    RecordButtonAction.START_RECORDING -> startRecording()
                    RecordButtonAction.STOP_RECORDING -> stopRecording()
                    RecordButtonAction.START_PLAYBACK -> {
                        if (_uiState.value.state == RecordState.RECORDED) {
                            startPlayback()
                        } else {
                            Log.d("RecordViewModel", " 재생 요청 무시됨: 아직 RECORDED 상태 아님")
                        }
                    }
                    RecordButtonAction.STOP_PLAYBACK -> stopPlayback()
                }
            }
            is RecordEvent.OnCancelClick -> {
                cancelRecording()
                _recordedFilePath.value = null
                _recorded.value = false
            }
            is RecordEvent.SaveRecording -> {
                saveRecording()
            }
            else -> null
        }


    }
    @SuppressLint("InlinedApi")
    private fun registerReceiver() {
        if (receiverRegistered) return

        val filter = IntentFilter(RecordService.ACTION_UPDATE)
        context.registerReceiver(updateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        receiverRegistered = true
    }
    override fun onCleared() {
        super.onCleared()
        if (receiverRegistered) {
            context.unregisterReceiver(updateReceiver)
            receiverRegistered = false
        }
    }

    fun startRecording() {
        sendCommand(RecordService.ACTION_START_RECORD)
    }

    fun stopRecording() {
        sendCommand(RecordService.ACTION_FINISH_RECORD)
    }

    fun startPlayback() {
        sendCommand(RecordService.ACTION_START_PLAY)
    }

    fun stopPlayback() {
        sendCommand(RecordService.ACTION_STOP_PLAY)
    }
    fun cancelRecording() {
        sendCommand(RecordService.ACTION_CANCEL_RECORD)
    }
    fun saveRecording() {
        sendCommand(RecordService.ACTION_SAVE_RECORDING)
    }



    private fun sendCommand(action: String) {
        val intent = Intent(context, RecordService::class.java).apply {
            this.action = action
        }
        if(action == RecordService.ACTION_START_RECORD)
            ContextCompat.startForegroundService(context, intent)
        else
            context.startService(intent)
    }
    fun showDialog() {
        showBottomRecordDialog()
    }
    private fun showBottomRecordDialog() {
        _bottomRecordDialogState.value = BottomRecordState(
            showDialog = true,
            onClickCancel = { resetBottomRecordDialog() }
        )
    }
    private fun resetBottomRecordDialog() {
        _bottomRecordDialogState.value = BottomRecordState()
    }
    private fun showDeleteRecordingAlert() {
        _recordingAlertDialogState.value = AlertDialogState(
            title = getString(context, com.androiddev.snsappwithcompose.R.string.confirm_delete_added_voice),
            confirmText = getString(context, com.androiddev.snsappwithcompose.R.string.confirm),
            cancelText = getString(context, com.androiddev.snsappwithcompose.R.string.cancel),
            onClickConfirm = {
                _recordedFilePath.value = null
                _recorded.value = false
                deletedAudio = prevRemotePath
                resetRecordingAlert()
            },
            onClickCancel = {
                resetRecordingAlert()
            }
        )
    }
    private fun resetRecordingAlert() {
        _recordingAlertDialogState.value = AlertDialogState()
    }

}
enum class RecordState { IDLE, RECORDING, RECORDED, PLAYING }
enum class RecordButtonAction {
    START_RECORDING,
    STOP_RECORDING,
    START_PLAYBACK,
    STOP_PLAYBACK
}
data class RecordUIState(
    val state: RecordState = RecordState.IDLE,
    val progress: Float = 0f,
    val elapsedMillis: Long = 0L,
    val formattedTime: String,
    val maxDurationMillis: Long = MAX_DURATION_MILLIS
) {
    val buttonAction: RecordButtonAction
        get() = when (state) {
            RecordState.IDLE -> RecordButtonAction.START_RECORDING
            RecordState.RECORDING -> RecordButtonAction.STOP_RECORDING
            RecordState.RECORDED -> RecordButtonAction.START_PLAYBACK
            RecordState.PLAYING -> RecordButtonAction.STOP_PLAYBACK
        }
}