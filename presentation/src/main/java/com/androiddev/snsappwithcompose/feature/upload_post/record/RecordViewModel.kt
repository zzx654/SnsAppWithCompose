package com.androiddev.snsappwithcompose.feature.upload_post.record

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.data.util.Constants
import com.androiddev.snsappwithcompose.service.record.RecordService
import com.androiddev.data.util.Constants.DEFAULT_ELAPSED_TIME
import com.androiddev.data.util.Constants.DEFAULT_PROGRESS
import com.androiddev.data.util.Constants.MAX_DURATION_MILLIS
import com.androiddev.data.util.FileUtil
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.viewmodel.BaseViewModel
import com.androiddev.snsappwithcompose.common.state.AlertDialogState
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.util.NotificationPermissionUtils
import com.androiddev.snsappwithcompose.service.record.RecordIntentKeys
import com.androiddev.snsappwithcompose.service.record.RecordStateConstants.STATE_IDLE
import com.androiddev.snsappwithcompose.service.record.RecordStateConstants.STATE_PLAYING
import com.androiddev.snsappwithcompose.service.record.RecordStateConstants.STATE_RECORDED
import com.androiddev.snsappwithcompose.service.record.RecordStateConstants.STATE_RECORDING
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class RecordViewModel @Inject constructor(
    @ApplicationContext context: Context,
): BaseViewModel(context) {
    var currentOutputFile: File? = null
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var startTime: Long = 0L
    private var maxDurationMillis = Constants.MAX_DURATION_MILLIS
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
    private val _uiState = MutableStateFlow(RecordUIState(formattedTime = getString(context, R.string.default_formatted_time)))
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

    private val _saveResult = MutableStateFlow<Boolean?>(null)
    val saveResult = _saveResult

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

            }
            is RecordEvent.SaveRecording -> {
                saveRecording()
            }
            else -> null
        }


    }
    override fun onCleared() {
        super.onCleared()
        if (receiverRegistered) {
            // context.unregisterReceiver(updateReceiver)
            receiverRegistered = false
        }
    }

    private fun startRecording() {
        val file = FileUtil.generateFile(context)
        currentOutputFile = file
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {  // API 34
            MediaRecorder(context)  // API 34 이상에서 사용 가능한 생성자
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()      // API 33 이하에서 사용하는 기본 생성자
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        startTime = System.currentTimeMillis()
        sendCommand(RecordService.ACTION_START_RECORD)
        startTimerLoop(STATE_RECORDING)
    }
    private fun startTimerLoop(stateStr: String) {
        timerJob = scope.launch {
            var lastUpdateSecond = -1L
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                val currentSecond = elapsed / 1000
                val progress = (elapsed.toFloat() / maxDurationMillis).coerceIn(0f, 1f)

                if (currentSecond != lastUpdateSecond) {
                    // 포맷된 시간
                    val seconds = (elapsed / 1000).toInt()
                    val minutes = seconds / 60
                    val secs = seconds % 60
                    val formattedTime = String.format("%02d:%02d", minutes, secs)

                    val intent = Intent(RecordService.ACTION_RECORD_STATUS).apply {
                        putExtra(RecordIntentKeys.STATE, stateStr)
                        putExtra(RecordIntentKeys.FORMATTED_TIME, formattedTime)
                    }
                    context.sendBroadcast(intent)
                    updateState(stateStr, elapsed, formattedTime, progress)
                    lastUpdateSecond = currentSecond
                } else {
                    // progress만 갱신 (formattedTime은 이전 값을 사용)
                    updateState(stateStr, elapsed, null, progress)
                }
                if (stateStr == STATE_RECORDING && elapsed >= maxDurationMillis) {
                    stopRecording()
                }

                delay(50L)
            }
        }
    }
    private fun updateState(state:String,elapsedMillis: Long,formattedTime: String?,progress: Float = DEFAULT_PROGRESS,filePath:String? = null) {

        _uiState.update {
            it.copy(
                state = RecordState.valueOf(state),
                elapsedMillis = elapsedMillis,
                formattedTime = formattedTime?:_uiState.value.formattedTime,
                progress = progress
            )
        }
        filePath?.let {
            _recordedFilePath.value = it
            _recorded.value = true
        }
    }
    private fun stopEverything() {
        maxDurationMillis = MAX_DURATION_MILLIS
        timerJob?.cancel()
        mediaRecorder?.apply {
            try {
                stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            release()
        }
        mediaRecorder = null

        mediaPlayer?.apply {
            try {
                stop()
            } catch (e: Exception) {}
            release()
        }
        mediaPlayer = null
    }
    private fun stopRecording() {
        stopEverything()
        updateState(
            state = STATE_RECORDED,
            elapsedMillis = 0L,
            formattedTime = getString(context,R.string.default_formatted_time),
            filePath = currentOutputFile?.absolutePath)
        sendCommand(RecordService.ACTION_FINISH_RECORD)
    }

    private fun startPlayback() {
        sendCommand(RecordService.ACTION_START_PLAY)
        currentOutputFile?.takeIf { it.exists() }?.let { file ->
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                start()
                val duration = duration.toLong() // 녹음 파일의 총 길이 (ms)
                maxDurationMillis = duration //  여기서 maxDurationMillis를 덮어씌움
                setOnCompletionListener {
                    stopPlayback()
                }
            }
            startTime = System.currentTimeMillis()
            startTimerLoop(STATE_PLAYING)
        }
    }

    private fun stopPlayback() {
        sendCommand(RecordService.ACTION_STOP_PLAY)
        stopEverything()
        updateState(STATE_RECORDED, DEFAULT_ELAPSED_TIME,getString(context,R.string.default_formatted_time))
    }
    private fun cancelRecording() {
        _recordedFilePath.value = null
        _recorded.value = false
        sendCommand(RecordService.ACTION_CANCEL_RECORD)
        stopEverything()
        updateState(STATE_IDLE, DEFAULT_ELAPSED_TIME,getString(context,R.string.default_formatted_time))

    }
    private fun saveRecording() {
        if(recordedFilePath.value == null) {
            _saveResult.value = false
            viewModelScope.launch {
                setEvent(
                    UiEvent.ShowToast(
                        message = getString(
                            context,
                            R.string.no_recording
                        )
                    )
                )
            }
            return
        }
        saveResult.value = true
        sendCommand(RecordService.ACTION_SAVE_RECORDING)
        stopEverything()
        updateState(STATE_IDLE, DEFAULT_ELAPSED_TIME,getString(context,R.string.default_formatted_time))
    }
    private fun sendCommand(action: String) {
        NotificationPermissionUtils.checkNotificationPermission(
            context = context,
            onGranted = {
                val intent = Intent(context, RecordService::class.java).apply {
                    this.action = action
                }
                if(action == RecordService.ACTION_START_RECORD)
                    ContextCompat.startForegroundService(context, intent)
                else
                    context.startService(intent)
            }
        )

    }
    fun showDialog() {
        showBottomRecordDialog()
    }
    fun resetSaveResult() {
        _saveResult.value = null
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
            title = getString(context, R.string.confirm_delete_added_voice),
            confirmText = getString(context, R.string.confirm),
            cancelText = getString(context, R.string.cancel),
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