package com.androiddev.snsappwithcompose.feature.createprofile.cropimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import com.androiddev.snsappwithcompose.common.util.encodeToBase64
import com.androiddev.snsappwithcompose.feature.createprofile.component.Corner
import com.androiddev.snsappwithcompose.feature.createprofile.component.getCroppedBitmap
import com.androiddev.snsappwithcompose.feature.createprofile.component.processBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class CropViewModel : ViewModel() {
    var image by mutableStateOf<ImageBitmap?>(null)
    var isCropping by mutableStateOf(false)
    var topLeft by mutableStateOf(Offset(160f, 370f))
    var topRight by mutableStateOf(Offset(560f, 370f))
    var bottomLeft by mutableStateOf(Offset(160f, 770f))
    var bottomRight by mutableStateOf(Offset(560f, 770f))

    var draggingCorner by mutableStateOf<Corner?>(null)
    var draggingCenter by mutableStateOf(false)

    fun loadImage(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val bitmap = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }

            bitmap?.let {
                withContext(Dispatchers.Main) {
                    // image에 값을 바로 할당합니다
                    image = processBitmap(uri, it, context)  // image = it.asImageBitmap()로 설정
                }
            }
        }
    }
    fun cropImage(
        image: ImageBitmap,
        topLeft: Offset,
        bottomRight: Offset,
        maxWidth: Float,
        maxHeight: Float,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            isCropping = true
            var croppedBitmap: Bitmap? = null
            withContext(Dispatchers.Default) {
                croppedBitmap = getCroppedBitmap(
                    image,
                    Rect(topLeft, bottomRight),
                    maxWidth,
                    maxHeight
                )
            }

            withContext(Dispatchers.Main) {
                // 크롭한 이미지를 Base64로 인코딩
                val base64String = croppedBitmap?.let { encodeToBase64(it, Bitmap.CompressFormat.JPEG, 100) }
                // 결과 콜백 호출
                onResult(base64String)
                isCropping = false
            }
        }
    }

    fun updateDraggingState(corner: Corner?, isCenterDragging: Boolean) {
        draggingCorner = corner
        draggingCenter = isCenterDragging
    }
}