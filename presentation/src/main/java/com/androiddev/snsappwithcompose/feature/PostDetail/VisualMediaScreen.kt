package com.androiddev.snsappwithcompose.feature.PostDetail

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.androiddev.domain.model.Media
import com.androiddev.domain.model.MediaType
import com.androiddev.snsappwithcompose.common.component.VisualMediaContent
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_IMAGE
import com.androiddev.snsappwithcompose.feature.upload_post.component.MediaItem

@Composable
fun VisualMediaScreen(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry
) {
    val args = navBackStackEntry.toRoute<Screen.MediaScreen>()
    val mediaItems = args.mediaItems?.map {
        MediaItem(
            remotePath = it.url,
            type = if(it.type == MEDIA_TYPE_IMAGE) MediaType.IMAGE else MediaType.VIDEO,
            remoteThumbnailPath = it.thumbnailUrl
        )
    }

    VisualMediaContent(
        media = mediaItems?:listOf(),
        onComplete = { navController.popBackStack() },
        onVideoClick = { encoded ->
            navController.navigate(
                Screen.VideoPlayerScreen(
                    encodedUri = encoded,
                ))
        }


    )
}