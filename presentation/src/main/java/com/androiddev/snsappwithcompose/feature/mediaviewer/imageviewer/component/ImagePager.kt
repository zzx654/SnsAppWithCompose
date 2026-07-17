package com.androiddev.snsappwithcompose.feature.mediaviewer.imageviewer.component


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.androiddev.domain.model.MediaPost


@Composable
fun ImagePager(
    images:List<MediaPost>,
    pagerState:PagerState,
    modifier: Modifier = Modifier,
    userScrollEnabled: Boolean = true,
    onTap:() -> Unit = {},
    onGestureStateChanged:(Boolean) -> Unit = {},
    onZoomStateChanged:(Boolean) -> Unit = {}
) {

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = userScrollEnabled,
        modifier = modifier
    ) { page ->
        val image = images[page]
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ZoomableImage(
                image = image,
                modifier = Modifier.fillMaxSize(),
                onTap = onTap,
                onZoomStateChanged = onZoomStateChanged,
                onGestureStateChanged = onGestureStateChanged

            )


        }

    }

}