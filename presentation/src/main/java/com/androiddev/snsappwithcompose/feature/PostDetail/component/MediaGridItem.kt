package com.androiddev.snsappwithcompose.feature.PostDetail.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.androiddev.domain.model.Media
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_IMAGE
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_VIDEO

@Composable
fun MediaGridItem(
    media: Media,
    modifier: Modifier = Modifier
) {
    when (media.type) {
        MEDIA_TYPE_IMAGE -> ImageItem(media, modifier)
        MEDIA_TYPE_VIDEO -> VideoItem(media, modifier)
    }
}