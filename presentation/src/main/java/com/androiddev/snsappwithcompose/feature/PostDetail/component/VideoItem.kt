package com.androiddev.snsappwithcompose.feature.PostDetail.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.androiddev.domain.model.Media
import com.androiddev.snsappwithcompose.BuildConfig

@Composable
fun VideoItem(media: Media, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        AsyncImage(
            model = BuildConfig.BASE_URL + (media.thumbnailUrl ?: media.url),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        /**Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.Center)
                .size(48.dp)
        )**/
    }
}