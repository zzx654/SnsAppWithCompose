package com.androiddev.snsappwithcompose.service.record

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.CHANNEL_ID_RECORD
import com.androiddev.snsappwithcompose.common.util.NotificationConstants.NOTIFICATION_ID_RECORD
import com.androiddev.snsappwithcompose.common.util.NotificationHelper
import com.androiddev.snsappwithcompose.service.record.RecordStateConstants.STATE_PLAYING
import com.androiddev.snsappwithcompose.service.record.RecordStateConstants.STATE_RECORDING
import com.androiddev.snsappwithcompose.service.record.RecordIntentKeys.STATE
import com.androiddev.snsappwithcompose.service.record.RecordIntentKeys.FORMATTED_TIME

class RecordService : Service() {

    companion object {
        const val ACTION_START_RECORD = RecordServiceActions.ACTION_START_RECORD
        const val ACTION_START_PLAY = RecordServiceActions.ACTION_START_PLAY
        const val ACTION_STOP_PLAY = RecordServiceActions.ACTION_STOP_PLAY
        const val ACTION_SAVE_RECORDING = RecordServiceActions.ACTION_SAVE_RECORDING
        const val ACTION_FINISH_RECORD = RecordServiceActions.ACTION_FINISH_RECORD
        const val ACTION_CANCEL_RECORD = RecordServiceActions.ACTION_CANCEL_RECORD
        const val ACTION_RECORD_STATUS = RecordServiceActions.ACTION_RECORD_STATUS
    }

    private lateinit var notificationHelper: NotificationHelper

    private val recordStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != ACTION_RECORD_STATUS) return

            val stateStr = intent.getStringExtra(STATE)
            val formattedTime = intent.getStringExtra(FORMATTED_TIME) ?: ""

            val content = when (stateStr) {
                STATE_RECORDING -> "${getString(R.string.recording_content)} $formattedTime"
                STATE_PLAYING -> "${getString(R.string.playing_content)} $formattedTime"
                else -> ""
            }

            if (content.isNotEmpty()) {
                notificationHelper.updateNotification(NOTIFICATION_ID_RECORD, content)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        registerStatusReceiver()
        Log.d("RecordService", "onCreate 호출됨")
    }

    private fun registerStatusReceiver() {
        val filter = IntentFilter(ACTION_RECORD_STATUS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(recordStatusReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(recordStatusReceiver, filter)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("RecordService", "onStartCommand: ${intent?.action}")
        when (intent?.action) {
            ACTION_START_RECORD -> startForegroundNotification(getString(R.string.recording_content))
            ACTION_START_PLAY -> startForegroundNotification(getString(R.string.playing_content))
            ACTION_FINISH_RECORD, ACTION_STOP_PLAY -> stopServiceNotification()
            ACTION_CANCEL_RECORD, ACTION_SAVE_RECORDING -> resetRecording()
        }
        return START_NOT_STICKY
    }

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

    private fun stopServiceNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
    }

    private fun resetRecording() {
        stopServiceNotification()
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        runCatching { unregisterReceiver(recordStatusReceiver) }
        notificationHelper.cancelNotification(NOTIFICATION_ID_RECORD)
        Log.d("RecordService", "onDestroy 호출됨")
    }
}