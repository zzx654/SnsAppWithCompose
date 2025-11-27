package com.androiddev.snsappwithcompose.service.record

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.androiddev.data.util.Constants
import com.androiddev.data.util.Constants.DEFAULT_ELAPSED_TIME
import com.androiddev.data.util.Constants.DEFAULT_PROGRESS
import com.androiddev.data.util.FileUtil
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_ID_RECORD
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.NOTIFICATION_ID_RECORD
import com.androiddev.snsappwithcompose.common.util.NotificationHelper
import com.androiddev.snsappwithcompose.service.record.RecordStateConstants.STATE_IDLE
import com.androiddev.snsappwithcompose.service.record.RecordStateConstants.STATE_PLAYING
import com.androiddev.snsappwithcompose.service.record.RecordStateConstants.STATE_RECORDED
import com.androiddev.snsappwithcompose.service.record.RecordStateConstants.STATE_RECORDING
import com.androiddev.snsappwithcompose.service.record.RecordIntentKeys.STATE
import com.androiddev.snsappwithcompose.service.record.RecordIntentKeys.PROGRESS
import com.androiddev.snsappwithcompose.service.record.RecordIntentKeys.ELAPSED
import com.androiddev.snsappwithcompose.service.record.RecordIntentKeys.FILE_PATH
import com.androiddev.snsappwithcompose.service.record.RecordIntentKeys.FORMATTED_TIME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class RecordService: Service() {
    companion object {
        const val ACTION_START_RECORD = RecordServiceActions.ACTION_START_RECORD
        const val ACTION_START_PLAY = RecordServiceActions.ACTION_START_PLAY
        const val ACTION_STOP_PLAY = RecordServiceActions.ACTION_STOP_PLAY
        const val ACTION_SAVE_RECORDING = RecordServiceActions.ACTION_SAVE_RECORDING
        const val ACTION_UPDATE = RecordServiceActions.ACTION_UPDATE
        const val ACTION_FINISH_RECORD = RecordServiceActions.ACTION_FINISH_RECORD
        const val ACTION_CANCEL_RECORD = RecordServiceActions.ACTION_CANCEL_RECORD

        var currentOutputFile: File? = null
    }
    private lateinit var notificationHelper:NotificationHelper
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var startTime: Long = 0L
    private var maxDurationMillis = Constants.MAX_DURATION_MILLIS

    private fun startForegroundNotification(content: String) {
        notificationHelper.createChannel(
            channelId = CHANNEL_ID_RECORD,
            channelName = getString(R.string.record_service_channel_name),
        )
        val notification = notificationHelper.createNotification(
            channelId = CHANNEL_ID_RECORD,
            contentTitle = getString(R.string.app_name),
            contentText = content,
            smallIcon = android.R.drawable.ic_btn_speak_now,
            isForegroundNotification = true
        )
        startForeground(NOTIFICATION_ID_RECORD, notification)
    }
    @SuppressLint("NewApi")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("RecordService", "onStartCommand: ${intent?.action}")
        when (intent?.action) {
            ACTION_START_RECORD -> startRecording()
            ACTION_FINISH_RECORD -> finishRecording()
            ACTION_START_PLAY -> startPlayback()
            ACTION_STOP_PLAY -> stopPlayback()
            ACTION_CANCEL_RECORD -> resetRecording()
            ACTION_SAVE_RECORDING -> resetRecording()
        }
        return START_STICKY
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording() {
        //stopEverything()//리소스 해제
        val file = FileUtil.generateFile(this)
        currentOutputFile = file
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {  // API 34
            MediaRecorder(this)  // API 34 이상에서 사용 가능한 생성자
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
        startForegroundNotification(getString(R.string.recording_content))

        startTime = System.currentTimeMillis()
        startTimerLoop(STATE_RECORDING)
    }


    @SuppressLint("DefaultLocale")
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

                    // 알림 텍스트 업데이트
                    val content = when (stateStr) {
                        STATE_RECORDING -> getString(R.string.recording_content) + " $formattedTime"
                        STATE_PLAYING -> getString(R.string.playing_content) + " $formattedTime"
                        else -> ""
                    }
                    notificationHelper.updateNotification(NOTIFICATION_ID_RECORD,content)
                    sendProgressUpdate(stateStr, elapsed, formattedTime, progress)
                    lastUpdateSecond = currentSecond
                } else {
                    // progress만 갱신 (formattedTime은 이전 값을 사용)
                    sendProgressUpdate(stateStr, elapsed, null, progress)
                }
                if (stateStr == STATE_RECORDING && elapsed >= maxDurationMillis) {
                    stopEverything()
                    sendProgressUpdate(STATE_RECORDED, DEFAULT_ELAPSED_TIME,getString(R.string.default_formatted_time))
                    stopSelf()
                }

                delay(50L)
            }
        }
    }
    private fun startPlayback() {
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
            startForegroundNotification(getString(R.string.playing_content))
            startTime = System.currentTimeMillis()
            startTimerLoop(STATE_PLAYING)
        }
    }
    private fun stopPlayback() {
        stopEverything()
        sendProgressUpdate(STATE_RECORDED, DEFAULT_ELAPSED_TIME,getString(R.string.default_formatted_time))
    }
    private fun finishRecording() {

        stopEverything()
        sendProgressUpdate(
            state = STATE_RECORDED,
            elapsed = 0L,
            formattedTime = getString(R.string.default_formatted_time),
            filePath = currentOutputFile?.absolutePath)
    }
    private fun resetRecording() {

        stopEverything()
        sendProgressUpdate(STATE_IDLE, DEFAULT_ELAPSED_TIME,getString(R.string.default_formatted_time))
        stopSelf()
    }
    @SuppressLint("NewApi")
    private fun stopEverything() {
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

        stopForeground(STOP_FOREGROUND_REMOVE);
    }
    private fun sendProgressUpdate(state: String, elapsed: Long,formattedTime:String?, progress: Float = DEFAULT_PROGRESS,filePath:String? = null) {
        val intent = Intent(ACTION_UPDATE).apply {
            putExtra(STATE, state)
            putExtra(ELAPSED, elapsed)
            putExtra(PROGRESS, progress)
            putExtra(FORMATTED_TIME,formattedTime)
            putExtra(FILE_PATH, filePath)
        }
        sendBroadcast(intent)
    }
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        notificationHelper.cancelNotification(NOTIFICATION_ID_RECORD)
    }
    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        Log.d("RecordService", " onCreate 호출됨")
    }
}