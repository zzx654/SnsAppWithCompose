package com.androiddev.snsappwithcompose.common.util

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

fun encodeToBase64(image: Bitmap, compressFormat: Bitmap.CompressFormat, quality: Int): String {
    val byteArrayOS = ByteArrayOutputStream()
    image.compress(compressFormat, quality, byteArrayOS)
    return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT)
}