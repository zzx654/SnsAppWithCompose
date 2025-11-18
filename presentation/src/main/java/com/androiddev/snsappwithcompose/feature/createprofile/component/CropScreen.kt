package com.androiddev.snsappwithcompose.feature.createprofile.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.component.LoadingDialogWithText
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.util.encodeToBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.math.min
import kotlin.math.roundToInt

enum class Corner {
    TopLeft, TopRight, BottomLeft, BottomRight
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CropScreen(navController: NavController, navBackStackEntry: NavBackStackEntry) {
    val args = navBackStackEntry.toRoute<Screen.CropScreen>()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var isCropping by remember { mutableStateOf(false) }
    val decodedUriString = Uri.decode(args.encodedUri)  // URI 디코딩
    val uri = Uri.parse(decodedUriString)  // 디코딩된 문자열을 Uri 객체로 파싱
    var image by remember { mutableStateOf<ImageBitmap?>(null) }


    // 이미지 로드하는 비동기 작업
    LaunchedEffect(uri) {
        scope.launch(Dispatchers.IO) {
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
                    image = processBitmap(uri, it, context)  // 처리 후 ImageBitmap으로 변환
                    isLoading = false  // 로딩 상태 종료
                }
            }
        }
    }



    var topLeft by remember { mutableStateOf(Offset(160f, 370f)) }
    var topRight by remember { mutableStateOf(Offset(560f, 370f)) }
    var bottomLeft by remember { mutableStateOf(Offset(160f, 770f)) }
    var bottomRight by remember { mutableStateOf(Offset(560f, 770f)) }

    var draggingCorner by remember { mutableStateOf<Corner?>(null) }
    var draggingCenter by remember { mutableStateOf(false) }

    // 크롭 중 표시
    LoadingDialogWithText(
        text = getString(context, R.string.cropping_alert),
        isLoading = { isCropping }
    )

    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
    ) {

        image?.let { bmap ->
            Image(
                bitmap = bmap,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        } ?: run {
            Text(getString(context,R.string.loading_bitmapimage), color = Color.White)
        }

        // 드래그를 통해 크롭 영역 지정
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset: Offset ->
                        draggingCorner = when {
                            offset.isNear(topLeft) -> Corner.TopLeft
                            offset.isNear(topRight) -> Corner.TopRight
                            offset.isNear(bottomLeft) -> Corner.BottomLeft
                            offset.isNear(bottomRight) -> Corner.BottomRight
                            else -> null
                        }
                        draggingCenter = draggingCorner == null &&
                                Rect(topLeft, bottomRight).contains(offset)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        when (draggingCorner) {
                            Corner.TopLeft -> {
                                topLeft += dragAmount
                                topRight = topRight.copy(y = topLeft.y)
                                bottomLeft = bottomLeft.copy(x = topLeft.x)
                            }

                            Corner.TopRight -> {
                                topRight += dragAmount
                                topLeft = topLeft.copy(y = topRight.y)
                                bottomRight = bottomRight.copy(x = topRight.x)
                            }

                            Corner.BottomLeft -> {
                                bottomLeft += dragAmount
                                topLeft = topLeft.copy(x = bottomLeft.x)
                                bottomRight = bottomRight.copy(y = bottomLeft.y)
                            }

                            Corner.BottomRight -> {
                                bottomRight += dragAmount
                                topRight = topRight.copy(x = bottomRight.x)
                                bottomLeft = bottomLeft.copy(y = bottomRight.y)
                            }

                            null -> if (draggingCenter) {
                                // Move the entire rectangle by adjusting all corners
                                topLeft += dragAmount
                                topRight += dragAmount
                                bottomLeft += dragAmount
                                bottomRight += dragAmount
                            }
                        }

                    },
                    onDragEnd = {
                        draggingCorner = null
                        draggingCenter = false
                    }
                )
            }) {
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                val rectSize = Size(
                    width = topRight.x - topLeft.x, height = bottomLeft.y - topLeft.y
                )
                // Destination
                drawRect(Color(0x77000000))
                // Draw the crop rectangle
                drawRect(
                    color = Color.Transparent, topLeft = topLeft, size = rectSize, blendMode = BlendMode.Clear
                )
                drawRect(
                    color = Color.White, topLeft = topLeft, size = rectSize, style = Stroke(width = 2f)
                )
                // Draw corner handles
                drawHandle(topLeft)
                drawHandle(topRight)
                drawHandle(bottomLeft)
                drawHandle(bottomRight)
                restoreToCount(checkPoint)
            }
        }
        Text(
            text = getString(context, R.string.confirm),
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(17.dp)
                .clickable {
                    scope.launch {
                        isCropping = true

                        var croppedBitmap:Bitmap? = null
                        withContext(Dispatchers.Default) {

                                croppedBitmap = getCroppedBitmap(
                                    image!!,
                                    Rect(topLeft, bottomRight),
                                    constraints.maxWidth.toFloat(),
                                    constraints.maxHeight.toFloat()
                                )
                        }
                        withContext(Dispatchers.Main) {
                            // 결과 처리
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                getString(context, R.string.encodedBitmap),
                                croppedBitmap?.let { encodeToBase64(it, Bitmap.CompressFormat.JPEG, 100) }

                            )
                            isCropping = false

                            navController.popBackStack()
                        }

                    }
                }
        )

        // Cancel Button
        Text(
            text = getString(context, R.string.cancel),
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(17.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
    }
}

fun Offset.isNear(point: Offset, threshold: Float = 50f): Boolean {
    return (this - point).getDistance() <= threshold
}

fun DrawScope.drawHandle(center: Offset) {
    drawCircle(
        color = Color.White, radius = 20f, center = center
    )
}

/**
 * Extracts the cropped area from the original image based on the given crop rectangle.
 *
 * @param imageBitmap The original image bitmap.
 * @param cropRect The crop rectangle in canvas coordinates.
 * @param canvasWidth The width of the canvas.
 * @param canvasHeight The height of the canvas.
 * @return A Bitmap representing the cropped area.
 */
fun getCroppedBitmap(
    imageBitmap: ImageBitmap,
    cropRect: Rect,
    canvasWidth: Float,
    canvasHeight: Float
): Bitmap {
    val bitmapWidth = imageBitmap.width.toFloat()
    val bitmapHeight = imageBitmap.height.toFloat()

    // Calculate scaling factors to fit the image within the canvas
    val widthRatio = canvasWidth / bitmapWidth
    val heightRatio = canvasHeight / bitmapHeight

    val scaleFactor = min(widthRatio, heightRatio) // Preserve aspect ratio

    // Calculate the actual displayed image dimensions within the canvas
    val displayedImageWidth = bitmapWidth * scaleFactor
    val displayedImageHeight = bitmapHeight * scaleFactor

    // Calculate the offset to center the image within the canvas
    val offsetX = (canvasWidth - displayedImageWidth) / 2
    val offsetY = (canvasHeight - displayedImageHeight) / 2

    // Map the crop rectangle coordinates from the canvas to the original image dimensions
    val cropLeft =
        ((cropRect.left - offsetX) / scaleFactor).roundToInt().coerceIn(0, bitmapWidth.toInt())
    val cropTop =
        ((cropRect.top - offsetY) / scaleFactor).roundToInt().coerceIn(0, bitmapHeight.toInt())
    val cropRight =
        ((cropRect.right - offsetX) / scaleFactor).roundToInt().coerceIn(0, bitmapWidth.toInt())
    val cropBottom =
        ((cropRect.bottom - offsetY) / scaleFactor).roundToInt().coerceIn(0, bitmapHeight.toInt())

    // Calculate the cropped area width and height
    val cropWidth = (cropRight - cropLeft).coerceAtLeast(1)  // Ensure minimum 1px width
    val cropHeight = (cropBottom - cropTop).coerceAtLeast(1)  // Ensure minimum 1px height

    // Create a cropped bitmap from the original bitmap using the calculated rectangle
    return Bitmap.createBitmap(
        imageBitmap.asAndroidBitmap(), cropLeft, cropTop, cropWidth, cropHeight
    )
}
fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
    var newWidth = maxWidth
    var newHeight = (maxWidth / aspectRatio).toInt()

    if (newHeight > maxHeight) {
        newHeight = maxHeight
        newWidth = (maxHeight * aspectRatio).toInt()
    }

    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}
@SuppressLint("NewApi")
fun isScreenshot(uri: Uri, context: Context): Boolean {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val exifInterface = ExifInterface(inputStream!!)

        // Exif 정보에서 카메라 모델을 가져옵니다.
        val cameraModel = exifInterface.getAttribute(ExifInterface.TAG_MODEL)

        // 카메라 모델 정보가 없거나 빈 값이면 스크린샷으로 간주
        return cameraModel == null || cameraModel.isEmpty()
    } catch (e: IOException) {
        e.printStackTrace()
        return false  // 예외 발생 시 스크린샷으로 간주
    }
}
fun processBitmap(uri: Uri, bitmap: Bitmap, context: Context): ImageBitmap {
    return if (isScreenshot(uri, context)) {
        bitmap.asImageBitmap()  // 스크린샷이라면 리사이즈하지 않고 그대로 사용
    } else {
        resizeBitmap(bitmap, 1024, 1024).asImageBitmap()  // 스크린샷이 아니면 리사이즈
    }
}