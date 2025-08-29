package com.androiddev.snsappwithcompose.upload_post

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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddev.data.service.RecordService
import com.androiddev.snsappwithcompose.util.BottomRecordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class RecordViewModel @Inject constructor(
    private val context: Context
): ViewModel() {
    private val _bottomRecordDialogState: MutableState<BottomRecordState> = mutableStateOf(
        BottomRecordState()
    )
    val bottomRecordDialogState: State<BottomRecordState>
        get() = _bottomRecordDialogState
    private val _uiState = MutableStateFlow(RecordUIState())
    val uiState: StateFlow<RecordUIState> = _uiState.asStateFlow()

    private var receiverRegistered = false


    val progress: StateFlow<Float> = uiState.map { it.elapsedMillis / 60f }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0f)


    private val _recordedFilePath: MutableState<String?> = mutableStateOf(null)
    val recordedFilePath: State<String?>
        get() = _recordedFilePath

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            intent ?: return
            when (intent.action) {
                RecordService.ACTION_UPDATE -> {
                    val stateStr = intent.getStringExtra("state") ?: return
                    val elapsed = intent.getLongExtra("elapsed", 0L)
                    val progress = intent.getFloatExtra("progress", 0f)
                    val formattedTime = intent.getStringExtra("formattedTime")
                    val filePath = intent.getStringExtra("file_path")
                    val state = RecordState.valueOf(stateStr)


                    filePath?.let {
                        _recordedFilePath.value = it
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

    fun onEvent(event: RecordEvent) {
        when(event) {
            is RecordEvent.OnAddRecordClick -> {
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
                //Recording,playback 상태일때는 주의 (끄면안된다고 토스트 띄우기)
                //IDLE일땐 그냥끄면됨
                //Recorded일때는 업데이트 해주고 끄면됨
                /**_uiState.update {
                    it.copy(
                        state = RecordState.IDLE,
                        elapsedMillis = 0L,
                        formattedTime = "0:00",
                        progress = 0f
                    )
                }**/
                cancelRecording()

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

    fun uploadRecording() {
        val file = RecordService.currentOutputFile
        if (file != null && file.exists()) {
            //viewModelScope.launch(Dispatchers.IO) {
             //   val success = FileUploader.uploadToServer(file)
                // 업로드 성공 처리 등
            //}
        }
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
    val formattedTime: String = "0:00",
    val maxDurationMillis: Long = 5 * 60 * 1000L
) {
    val buttonAction: RecordButtonAction
        get() = when (state) {
            RecordState.IDLE -> RecordButtonAction.START_RECORDING
            RecordState.RECORDING -> RecordButtonAction.STOP_RECORDING
            RecordState.RECORDED -> RecordButtonAction.START_PLAYBACK
            RecordState.PLAYING -> RecordButtonAction.STOP_PLAYBACK
        }
}