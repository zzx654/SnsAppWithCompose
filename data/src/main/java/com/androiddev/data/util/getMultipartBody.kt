package com.androiddev.data.util

import android.content.Context
import android.net.Uri
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File

fun getMultipartBody(imageUri: Uri, context: Context): MultipartBody.Part {
    val file = File(getRealPathFromURI(imageUri, context))
    val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("image", file.name, requestBody)
}