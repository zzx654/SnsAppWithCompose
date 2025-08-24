package com.androiddev.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.androiddev.data.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RecordService: Service() {
    private var timer: Job? = null
    private var elapsed = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {

            "START" -> {
                startForegroundService()
            }
            "STOP" -> stopSelf()
        }
        return START_STICKY
    }
    private fun startForegroundService() {

        Log.d("RecordService", "startForegroundService called")
        val notification = NotificationCompat.Builder(this, "recording_channel")
            .setContentTitle("Recording...")
            .setSmallIcon(R.drawable.baseline_mic_none_24)
            .setOngoing(true)
            .build()

        startForeground(1, notification)

        elapsed = 0
        timer = CoroutineScope(Dispatchers.Default).launch {
            while (elapsed < 60) {
                delay(1000)
                elapsed++
                sendElapsedTime()
            }
            stopSelf()
        }
    }
    private fun sendElapsedTime() {
        val intent = Intent("RECORDING_ELAPSED").apply {
            putExtra("elapsed", elapsed)
        }
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }


}