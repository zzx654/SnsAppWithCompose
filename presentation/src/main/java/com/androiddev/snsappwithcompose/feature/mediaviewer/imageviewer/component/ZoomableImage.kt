package com.androiddev.snsappwithcompose.feature.mediaviewer.imageviewer.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil3.compose.SubcomposeAsyncImage
import com.androiddev.domain.model.MediaPost
import com.androiddev.snsappwithcompose.BuildConfig
import com.androiddev.snsappwithcompose.feature.mediaviewer.imageviewer.gesture.PinchToZoom

@Composable
fun ZoomableImage(
    image: MediaPost,
    modifier: Modifier = Modifier,
    onTap:() -> Unit = {},
    onZoomStateChanged: (Boolean) -> Unit = {},
    onGestureStateChanged:(Boolean) -> Unit = {},
) {



    PinchToZoom(
        modifier = modifier,
        onGestureStateChanged = onGestureStateChanged,
        onZoomStateChanged = onZoomStateChanged
    ) { scale,offset ->


        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {

                                onTap()

                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = BuildConfig.BASE_URL+image.url,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().graphicsLayer {
                    scaleX = scale
                    scaleY = scale

                    translationX = offset.x
                    translationY = offset.y
                },
                contentScale = ContentScale.Fit,
                loading = {
                    ImagePlaceholder()
                },
                error = {
                    ImagePlaceholder()
                }
            )


        }

    }



}