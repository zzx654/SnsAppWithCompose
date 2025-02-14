package com.androiddev.snsappwithcompose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.util.DebugLogger
import com.androiddev.snsappwithcompose.BuildConfig

@Composable
fun TempImage(
    imageUrl: ()-> String?
) {
    val imageLoader = LocalContext.current.imageLoader.newBuilder()
        .logger(DebugLogger())
        .build()
    if(imageUrl()!=null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(BuildConfig.BASE_URL+imageUrl())
                .build(),
            imageLoader = imageLoader,
            contentDescription = null
        )
    }
}