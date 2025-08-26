package com.androiddev.data.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.androiddev.data.R
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
        const val ACTION_START_RECORD = "ACTION_START_RECORD"
        const val ACTION_START_PLAY = "ACTION_START_PLAY"
        const val ACTION_STOP_PLAY = "ACTION_STOP_PLAY"
        const val ACTION_UPDATE = "ACTION_UPDATE"
        const val ACTION_FINISH_RECORD = "ACTION_FINISH_RECORD"

        var currentOutputFile: File? = null
    }
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val notificationId = 1
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var startTime: Long = 0L
    private val maxDurationMillis = 5 * 60 * 1000L

    private fun startForegroundNotification(content: String) {
        val channelId = "audio_channel"
        val channelName = "오디오 서비스"

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

        notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("오디오 앱")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOnlyAlertOnce(true)

        startForeground(notificationId, notificationBuilder.build())
    }
    private fun updateNotification(content: String) {
        notificationBuilder.setContentText(content)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_RECORD -> startRecording()
            ACTION_FINISH_RECORD -> finishRecording()
            ACTION_START_PLAY -> startPlayback()
            ACTION_STOP_PLAY -> stopPlayback()
        }
        return START_STICKY
    }
    private fun startRecording() {
        //stopEverything()//리소스 해제

        startForegroundNotification("녹음 중...")

        startTime = System.currentTimeMillis()
        startTimerLoop("RECORDING")
    }

    private fun startTimerLoop(stateStr: String) {
        timerJob = scope.launch {
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = (elapsed.toFloat() / maxDurationMillis).coerceIn(0f, 1f)

                // 포맷된 시간
                val seconds = (elapsed / 1000).toInt()
                val minutes = seconds / 60
                val secs = seconds % 60
                val formattedTime = String.format("%02d:%02d", minutes, secs)

                // 알림 텍스트 업데이트
                val content = when (stateStr) {
                    "RECORDING" -> "녹음 중... $formattedTime"
                    "PLAYING" -> "재생 중... $formattedTime"
                    else -> ""
                }
                updateNotification(content)

                // 상태 업데이트 브로드캐스트
                sendProgressUpdate(stateStr, elapsed, formattedTime, progress)

                if (stateStr == "RECORDING" && elapsed >= maxDurationMillis) {
                    //stopEverything()
                    sendProgressUpdate("RECORDED", 0L,"0:00")
                    stopSelf()
                }

                delay(1000L) // 매 1초마다 업데이트
            }
        }
    }
    private fun startPlayback() {
        startForegroundNotification("재생 중...")

        startTime = System.currentTimeMillis()
        startTimerLoop("PLAYING")
    }
    private fun stopPlayback() {
        stopEverything()
        sendProgressUpdate("RECORDED", 0L,"0:00")
        stopSelf()
    }
    @SuppressLint("NewApi")
    private fun stopEverything() {
        timerJob?.cancel()

        stopForeground(Service.STOP_FOREGROUND_REMOVE);
    }
    private fun sendProgressUpdate(state: String, elapsed: Long,formattedTime:String, progress: Float = 0f) {
        val intent = Intent(ACTION_UPDATE).apply {
            putExtra("state", state)
            putExtra("elapsed", elapsed)
            putExtra("progress", progress)
            putExtra("formattedTime",formattedTime)
        }
        sendBroadcast(intent)
    }
    override fun onBind(intent: Intent?): IBinder? = null




    private fun finishRecording() {
        stopEverything()
        sendProgressUpdate("RECORDED", 0L,"0:00")
        stopSelf()
    }
    private fun resetAll() {
        // 1) 진행 중인 녹음, 재생 모두 중지 및 리소스 해제
        stopEverything()

        // 2) 녹음 파일 삭제 (파일 존재하면)
        currentOutputFile?.let { file ->
            if (file.exists()) {
                file.delete()
            }
        }
        currentOutputFile = null

        // 3) 상태 초기화 및 UI 등으로 상태 전송
        sendProgressUpdate("IDLE", 0L, "0:00",0f)  // 상태 'IDLE' 등 원하는 초기 상태로

        // 4) 서비스 종료
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        //timer?.cancel()
    }


}