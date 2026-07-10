package com.androiddev.snsappwithcompose.feature.mediaviewer.component

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
    modifier: Modifier = Modifier
) {

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        val image = images[page]
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            SubcomposeAsyncImage(
                model = BuildConfig.BASE_URL+image.url,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit,
                loading = {
                    ImagePlaceholder()
                },
                error = {
                    ImagePlaceholder()
                }
            )

            //Text(text = "${page + 1} / ${images.size}",color = Color.White)

        }

    }

}