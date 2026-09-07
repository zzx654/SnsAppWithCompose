package com.androiddev.snsappwithcompose.common.util

import android.content.Context
import android.net.Uri
import com.androiddev.domain.model.MediaType
import com.androiddev.snsappwithcompose.feature.upload_post.component.MediaItem
import com.androiddev.snsappwithcompose.feature.upload_post.util.getVideoThumbnail
import com.androiddev.snsappwithcompose.feature.upload_post.util.isVideo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaItemFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun createMediaItems(uris: List<Uri>): List<MediaItem> = withContext(Dispatchers.IO) {
        uris.map { uri ->
            val isVideo = isVideo(context, uri)
            val thumbnail = if (isVideo) getVideoThumbnail(context, uri) else null

            MediaItem(
                uri = uri,
                type = if (isVideo) MediaType.VIDEO else MediaType.IMAGE,
                thumbnail = thumbnail,
                isNew = true
            )
        }
    }
}