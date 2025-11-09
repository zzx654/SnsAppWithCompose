package com.androiddev.data.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

fun getImageUri(context: Context, image: Bitmap?): Uri {
    val imagesFolder = File(context.cacheDir, "images")
    lateinit var uri: Uri
    try {
        imagesFolder.mkdirs()
        val file = File(imagesFolder, "Title - ${Calendar.getInstance().time}")
        val stream = FileOutputStream(file)
        image?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
        uri = FileProvider.getUriForFile(context.applicationContext, "com.androiddev.snsappwithcompose.fileprovider", file)

    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return uri
}
