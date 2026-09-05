package com.androiddev.snsappwithcompose.service.audio

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.androiddev.domain.audio.RecordServiceController
import com.androiddev.snsappwithcompose.common.util.NotificationPermissionUtils
import com.androiddev.snsappwithcompose.service.record.RecordIntentKeys
import com.androiddev.snsappwithcompose.service.record.RecordService
import javax.inject.Inject

class RecordServiceControllerImpl @Inject constructor(
    private val context: Context
) : RecordServiceController {

    override fun startRecordService() = sendCommand(RecordService.ACTION_START_RECORD)
    override fun finishRecordService() = sendCommand(RecordService.ACTION_FINISH_RECORD)
    override fun startPlayService() = sendCommand(RecordService.ACTION_START_PLAY)
    override fun stopPlayService() = sendCommand(RecordService.ACTION_STOP_PLAY)
    override fun cancelRecordService() = sendCommand(RecordService.ACTION_CANCEL_RECORD)
    override fun saveRecordService() = sendCommand(RecordService.ACTION_SAVE_RECORDING)

    override fun sendStatusBroadcast(stateStr: String, formattedTimeStr: String) {
        val intent = Intent(RecordService.ACTION_RECORD_STATUS).apply {
            putExtra(RecordIntentKeys.STATE, stateStr)
            putExtra(RecordIntentKeys.FORMATTED_TIME, formattedTimeStr)
        }
        context.sendBroadcast(intent)
    }

    private fun sendCommand(action: String) {
        NotificationPermissionUtils.checkNotificationPermission(
            context = context,
            onGranted = {
                val intent = Intent(context, RecordService::class.java).apply {
                    this.action = action
                }
                if (action == RecordService.ACTION_START_RECORD) {
                    ContextCompat.startForegroundService(context, intent)
                } else {
                    context.startService(intent)
                }
            }
        )
    }
}