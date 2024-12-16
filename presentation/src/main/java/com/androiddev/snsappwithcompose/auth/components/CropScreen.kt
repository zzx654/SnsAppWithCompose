package com.androiddev.snsappwithcompose.auth.components

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.androiddev.snsappwithcompose.util.Screen
import java.io.IOException
import kotlin.math.min
import kotlin.math.roundToInt

enum class Corner{
    TopLeft, TopRight, BottomLeft, BottomRight
}
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CropScreen(navController: NavController, navBackStackEntry: NavBackStackEntry) {
    var args = navBackStackEntry.toRoute<Screen.CropScreen>()
    val context = LocalContext.current
    val uri = args.encodedUri.replace("%3A",":").replace("%2F","/").let(Uri::parse)

    var image by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    try{
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P) {
            image = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver,uri)).asImageBitmap()
        } else {
            val bmap = MediaStore.Images.Media.getBitmap(context.contentResolver,uri)
            image = bmap.asImageBitmap()
        }
    }catch(e: IOException) {
        e.printStackTrace()
    }
    var topLeft by remember { mutableStateOf(Offset(160f,370f)) }
    var topRight by remember { mutableStateOf(Offset(560f,370f))}
    var bottomLeft by remember { mutableStateOf(Offset(160f,770f)) }
    var bottomRight by remember { mutableStateOf(Offset(560f,770f))}

    var draggingCorner by remember {
        mutableStateOf<Corner?>(null)
    }
    var draggingCenter by remember{
        mutableStateOf(false)
    }
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            bitmap = image!!,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {offset: Offset ->
                                  draggingCorner = when{
                                      offset.isNear(topLeft) -> Corner.TopLeft
                                      offset.isNear(topRight) -> Corner.TopRight
                                      offset.isNear(bottomLeft) -> Corner.BottomLeft
                                      offset.isNear(bottomRight) -> Corner.BottomRight
                                      else -> null
                                  }
                        draggingCenter = draggingCorner == null &&
                                Rect(topLeft,bottomRight).contains(offset)

                    },
                    onDrag = {change, dragAmount ->
                             change.consume()
                        when(draggingCorner) {
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
                        println("topLeft:$topLeft")
                        println("topRight:$topRight")
                        println("bottomLeft:$bottomLeft")
                        println("bottomRight:$bottomRight")

                    },
                    onDragEnd = {
                        draggingCorner = null
                        draggingCenter = false
                    }
                )
            }){
            val canvasWidth = size.width
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
                    color = Color.White, topLeft = topLeft, size = rectSize,style = Stroke(width = 2f)
                )
                // Draw corner handles
                drawHandle(topLeft)
                drawHandle(topRight)
                drawHandle(bottomLeft)
                drawHandle(bottomRight)
                restoreToCount(checkPoint)
            }




        }
        Button(
            onClick = {
                val croppedBitmap = getCroppedBitmap(
                    image!!,
                    Rect(topLeft, bottomRight),
                    canvasWidth = constraints.maxWidth.toFloat(),
                    canvasHeight = constraints.maxHeight.toFloat()
                )

                //set the cropped image to the composable
                image = croppedBitmap.asImageBitmap()
            }, modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(text = "Show Cropped Image")
        }
    }
    //Box(
     //   modifier = Modifier.fillMaxSize()
    //) {
     //   AsyncImage(
      //      model = ImageRequest.Builder(context).data(uri).build(),
       //     contentDescription = null,
        //    contentScale = ContentScale.Crop,
         //   modifier = Modifier.fillMaxSize()
        //)
   // }



}
fun Offset.isNear(point:Offset, threshold:Float = 50f):Boolean {
    return (this-point).getDistance() <= threshold
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
    imageBitmap: ImageBitmap,   // The original ImageBitmap
    cropRect: Rect,             // The crop rectangle area on the canvas
    canvasWidth: Float,         // The width of the canvas where the image is displayed
    canvasHeight: Float         // The height of the canvas where the image is displayed
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