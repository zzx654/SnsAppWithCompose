package com.androiddev.snsappwithcompose.feature.mediaviewer.gesture

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

@Composable
fun PinchToZoom(
    modifier: Modifier = Modifier,
    onScaleChanged: (Float) -> Unit = {},
    onDrag: (Offset) -> Boolean = { false },
    onGestureStateChanged: (Boolean) -> Unit = {},
    onZoomStateChanged: (Boolean) -> Unit = {},
    content: @Composable BoxScope.(
        scale: Float,
        offset: Offset
    ) -> Unit
) {
    var containerSize by remember {
        mutableStateOf(IntSize.Zero)
    }
    var scale by remember {
        mutableFloatStateOf(1f)
    }

    var offset by remember {
        mutableStateOf(Offset.Zero)
    }

    Box(
        modifier = modifier
            .onSizeChanged {
                containerSize = it
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    var isZoomGesture = false
                    var isDragGesture = false
                    var hasMoved = false

                    val touchSlop = viewConfiguration.touchSlop

                    awaitFirstDown(requireUnconsumed = false)




                    do {

                        val event = awaitPointerEvent()
                        if (event.changes.size > 1) {

                            onGestureStateChanged(true)


                            val zoom = event.calculateZoom()
                            val pan = event.calculatePan()

                            scale = (scale * zoom).coerceIn(1f, 5f)

                            if (scale > 1f) {
                                offset = clampOffset(
                                    offset + pan,
                                    scale,
                                    containerSize
                                )
                            } else {
                                scale = 1f
                                offset = Offset.Zero

                                hasMoved = false
                                offset = Offset.Zero
                            }

                            onScaleChanged(scale)
                            onZoomStateChanged(scale > 1.01f)

                            if (scale > 1.01f) {
                                //확대됨
                                event.changes.forEach {
                                    if (it.positionChanged()) {
                                        it.consume()
                                    }
                                }
                            }
                        } else if( scale > 1.01f) {
                            val change = event.changes.first()



                            val dragAmount = change.positionChange()
                            if (!hasMoved &&
                                dragAmount.getDistance() > touchSlop
                            ) {
                                hasMoved = true
                                onGestureStateChanged(true)
                            }
                            offset = clampOffset(
                                offset + dragAmount,
                                scale,
                                containerSize
                            )

                            val shouldConsume = onDrag(dragAmount)

                            if (shouldConsume) {
                                change.consume()
                            }
                        }

                    } while (event.changes.any { it.pressed })
                    onGestureStateChanged(false)


                }


            }
    ) {
        content(
            scale,
            offset
        )
    }

}

private fun clampOffset(
    offset: Offset,
    scale: Float,
    containerSize: IntSize
): Offset {

    if (containerSize == IntSize.Zero) return Offset.Zero

    val maxX =
        (containerSize.width * (scale - 1f)) / 2f

    val maxY =
        (containerSize.height * (scale - 1f)) / 2f

    return Offset(
        x = offset.x.coerceIn(-maxX, maxX),
        y = offset.y.coerceIn(-maxY, maxY)
    )
}

