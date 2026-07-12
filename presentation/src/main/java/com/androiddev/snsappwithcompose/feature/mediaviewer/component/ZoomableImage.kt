package com.androiddev.snsappwithcompose.feature.mediaviewer.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.androiddev.domain.model.MediaPost
import com.androiddev.snsappwithcompose.BuildConfig
import com.androiddev.snsappwithcompose.feature.mediaviewer.gesture.PinchToZoom

@Composable
fun ZoomableImage(
    image: MediaPost,
    modifier: Modifier = Modifier,
    onTap:() -> Unit = {},
    onScaleChanged: (Float) -> Unit = {}
) {




    PinchToZoom(
        modifier = modifier,
        onScaleChanged = onScaleChanged,
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