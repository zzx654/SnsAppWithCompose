package com.androiddev.domain.audio

interface RecordServiceController {
    fun startRecordService()
    fun finishRecordService()
    fun startPlayService()
    fun stopPlayService()
    fun cancelRecordService()
    fun saveRecordService()
    fun sendStatusBroadcast(stateStr: String, formattedTimeStr: String)
}