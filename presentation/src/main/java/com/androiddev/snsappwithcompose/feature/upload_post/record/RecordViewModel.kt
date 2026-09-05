package com.androiddev.snsappwithcompose.feature.upload_post.record


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.androiddev.data.util.Constants.DEFAULT_ELAPSED_TIME
import com.androiddev.data.util.Constants.DEFAULT_PROGRESS
import com.androiddev.data.util.Constants.MAX_DURATION_MILLIS
import com.androiddev.domain.audio.AudioFileManager
import com.androiddev.domain.audio.AudioPlayer
import com.androiddev.domain.audio.AudioRecorder
import com.androiddev.domain.audio.RecordServiceController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import com.androiddev.snsappwithcompose.common.state.AlertDialogStateV2
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.service.record.RecordStateConstants.STATE_IDLE
import com.androiddev.snsappwithcompose.service.record.RecordStateConstants.STATE_PLAYING
import com.androiddev.snsappwithcompose.service.record.RecordStateConstants.STATE_RECORDED
import com.androiddev.snsappwithcompose.service.record.RecordStateConstants.STATE_RECORDING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private val audioRecorder: AudioRecorder,
    private val audioPlayer: AudioPlayer,
    private val recordServiceController: RecordServiceController,
    private val audioFileManager: AudioFileManager
) : BaseViewModel() {

    var currentOutputFile: File? = null
    private var timerJob: Job? = null

    private var startTime: Long = 0L
    private var maxDurationMillis = MAX_DURATION_MILLIS

    private val _alertDialogState = MutableStateFlow(AlertDialogStateV2())
    val alertDialogState: StateFlow<AlertDialogStateV2> = _alertDialogState.asStateFlow()

    private val _uiState = MutableStateFlow(RecordUIState())
    val uiState: StateFlow<RecordUIState> = _uiState.asStateFlow()

    var prevRemotePath: String? = null
        private set
    var deletedAudio: String? = null
        private set

    private val _saveResult = MutableStateFlow<Boolean?>(null)
    val saveResult: StateFlow<Boolean?> = _saveResult.asStateFlow()

    fun initRecordState(remotePath: String) {
        _uiState.update { it.copy(recorded = true) }
        prevRemotePath = remotePath
    }

    fun onEvent(event: RecordEvent) {
        when (event) {
            is RecordEvent.OnAddRecordClick -> {
                if (uiState.value.recorded) {
                    showDeleteRecordingAlert()
                } else {
                    showDialog()
                }
            }
            is RecordEvent.RecordPlayBack -> {
                when (_uiState.value.buttonAction) {
                    RecordButtonAction.START_RECORDING -> startRecording()
                    RecordButtonAction.STOP_RECORDING -> stopRecording()
                    RecordButtonAction.START_PLAYBACK -> {
                        if (_uiState.value.state == RecordState.RECORDED) {
                            startPlayback()
                        } else {
                            Log.d("RecordViewModel", "재생 요청 무시됨: 아직 RECORDED 상태 아님")
                        }
                    }
                    RecordButtonAction.STOP_PLAYBACK -> stopPlayback()
                }
            }
            is RecordEvent.OnCancelClick -> cancelRecording()
            is RecordEvent.SaveRecording -> saveRecording()
            else -> Unit
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopEverything()
    }

    private fun startRecording() {
        val file = audioFileManager.generateRecordFile()
        currentOutputFile = file

        runCatching {
            audioRecorder.start(file)
        }.onSuccess {
            startTime = System.currentTimeMillis()
            recordServiceController.startRecordService()
            startTimerLoop(STATE_RECORDING)
        }.onFailure { e ->
            Log.e("RecordViewModel", "녹음 시작 실패", e)
            stopEverything()
        }
    }

    private fun startTimerLoop(stateStr: String) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var lastUpdateSecond = -1L
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                val currentSecond = elapsed / 1000
                val progress = (elapsed.toFloat() / maxDurationMillis).coerceIn(0f, 1f)

                if (currentSecond != lastUpdateSecond) {
                    val formattedTimeStr = elapsed.toFormattedTime()

                    recordServiceController.sendStatusBroadcast(stateStr, formattedTimeStr)
                    updateState(
                        state = stateStr,
                        elapsedMillis = elapsed,
                        formattedTime = UiText.DynamicString(formattedTimeStr),
                        progress = progress
                    )
                    lastUpdateSecond = currentSecond
                } else {
                    updateState(stateStr, elapsed, null, progress)
                }

                if (stateStr == STATE_RECORDING && elapsed >= maxDurationMillis) {
                    stopRecording()
                    break
                }

                delay(50L)
            }
        }
    }

    private fun updateState(
        state: String,
        elapsedMillis: Long,
        formattedTime: UiText? = null,
        progress: Float = DEFAULT_PROGRESS,
        filePath: String? = null
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                state = RecordState.valueOf(state),
                elapsedMillis = elapsedMillis,
                formattedTime = formattedTime ?: currentState.formattedTime,
                recordedFilePath = filePath ?: currentState.recordedFilePath,
                progress = progress,
                recorded = if (filePath != null) true else currentState.recorded
            )
        }
    }

    private fun stopEverything() {
        maxDurationMillis = MAX_DURATION_MILLIS
        timerJob?.cancel()
        timerJob = null

        audioRecorder.stop()
        audioPlayer.stop()
    }

    private fun stopRecording() {
        stopEverything()
        updateState(
            state = STATE_RECORDED,
            elapsedMillis = 0L,
            formattedTime = UiText.StringResource(R.string.default_formatted_time),
            filePath = currentOutputFile?.absolutePath
        )
        recordServiceController.finishRecordService()
    }

    private fun startPlayback() {
        val file = currentOutputFile?.takeIf { it.exists() } ?: return
        recordServiceController.startPlayService()

        runCatching {
            audioPlayer.start(file) {
                viewModelScope.launch {
                    stopPlayback()
                }

            }
        }.onSuccess { duration ->
            maxDurationMillis = duration
            startTime = System.currentTimeMillis()
            startTimerLoop(STATE_PLAYING)
        }.onFailure { e ->
            Log.e("RecordViewModel", "재생 시작 실패", e)
            stopPlayback()
        }
    }

    private fun stopPlayback() {
        recordServiceController.stopPlayService()
        stopEverything()
        updateState(
            state = STATE_RECORDED,
            elapsedMillis = DEFAULT_ELAPSED_TIME,
            formattedTime = UiText.StringResource(R.string.default_formatted_time)
        )
    }

    private fun cancelRecording() {
        _uiState.update {
            it.copy(
                recordedFilePath = null,
                recorded = false
            )
        }
        recordServiceController.cancelRecordService()
        stopEverything()
        updateState(
            state = STATE_IDLE,
            elapsedMillis = DEFAULT_ELAPSED_TIME,
            formattedTime = UiText.StringResource(R.string.default_formatted_time)
        )
    }

    private fun saveRecording() {
        if (uiState.value.recordedFilePath == null) {
            _saveResult.value = false
            return
        }
        _saveResult.value = true
        recordServiceController.saveRecordService()
        stopEverything()
        updateState(
            state = STATE_IDLE,
            elapsedMillis = DEFAULT_ELAPSED_TIME,
            formattedTime = UiText.StringResource(R.string.default_formatted_time)
        )
    }

    fun showDialog() {
        showBottomRecordDialog()
    }

    fun resetSaveResult() {
        _saveResult.value = null
    }

    private fun showBottomRecordDialog() {
        _uiState.update {
            it.copy(
                bottomRecordDialogState = BottomRecordState(
                    showDialog = true,
                    onClickCancel = { resetBottomRecordDialog() }
                )
            )
        }
    }

    private fun resetBottomRecordDialog() {
        _uiState.update {
            it.copy(bottomRecordDialogState = BottomRecordState())
        }
    }

    private fun showDeleteRecordingAlert() {
        _alertDialogState.value = AlertDialogStateV2(
            title = UiText.StringResource(R.string.confirm_delete_added_voice),
            confirmText = UiText.StringResource(R.string.confirm),
            cancelText = UiText.StringResource(R.string.cancel),
            onClickConfirm = {
                _uiState.update {
                    it.copy(
                        recordedFilePath = null,
                        recorded = false
                    )
                }
                deletedAudio = prevRemotePath
                resetRecordingAlert()
            },
            onClickCancel = { resetRecordingAlert() }
        )
    }

    private fun resetRecordingAlert() {
        _alertDialogState.value = AlertDialogStateV2()
    }
}

private fun Long.toFormattedTime(): String {
    val seconds = (this / 1000).toInt()
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}
enum class RecordState { IDLE, RECORDING, RECORDED, PLAYING }
