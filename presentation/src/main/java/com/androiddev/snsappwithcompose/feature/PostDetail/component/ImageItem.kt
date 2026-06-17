package com.androiddev.snsappwithcompose.feature.PostDetail.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.androiddev.domain.model.Media
import com.androiddev.snsappwithcompose.BuildConfig

@Composable
fun ImageItem(mediaUrl: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = BuildConfig.BASE_URL+ mediaUrl,
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}