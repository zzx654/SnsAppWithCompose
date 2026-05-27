package com.androiddev.snsappwithcompose.feature.PostDetail.component

import androidx.compose.runtime.Composable
import com.androiddev.domain.model.Media

@Composable
fun SingleMedia(media: Media) {
    MediaGridItem(media)
}