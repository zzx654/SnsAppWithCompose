package com.androiddev.snsappwithcompose.feature.mediaviewer.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.SubcomposeAsyncImage
import com.androiddev.domain.model.MediaPost
import com.androiddev.snsappwithcompose.BuildConfig

@Composable
fun ImagePager(
    images:List<MediaPost>,
    pagerState:PagerState,
    modifier: Modifier = Modifier,
    userScrollEnabled: Boolean = true,
    onTap:() -> Unit = {},
    onScaleChanged: (Float) -> Unit = {}
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
                onScaleChanged = onScaleChanged

            )


            //Text(text = "${page + 1} / ${images.size}",color = Color.White)

        }

    }

}