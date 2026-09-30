package com.androiddev.data.MediaItem

import android.content.Context
import android.net.Uri
import com.androiddev.data.util.getVideoThumbnail
import com.androiddev.data.util.isVideo
import com.androiddev.domain.model.MediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaItemFactory @Inject constructor(
    @ApplicationContext private val context: Context
)  {

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