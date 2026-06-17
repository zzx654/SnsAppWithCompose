package com.androiddev.snsappwithcompose.feature.PostDetail.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.androiddev.domain.model.Media
import com.androiddev.domain.model.MediaType
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_IMAGE
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_VIDEO
import com.androiddev.snsappwithcompose.feature.upload_post.component.MediaItem

@Composable
fun MediaGridItem(
    media: Media,
    modifier: Modifier = Modifier
) {
    when (media.type) {
        MEDIA_TYPE_IMAGE -> ImageItem(media.url, modifier)
        MEDIA_TYPE_VIDEO -> VideoItem(media.thumbnailUrl?:"", modifier)
        else -> null
    }
}