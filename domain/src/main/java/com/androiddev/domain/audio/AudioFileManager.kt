package com.androiddev.domain.audio

import java.io.File

interface AudioFileManager {
    fun generateRecordFile(): File
}