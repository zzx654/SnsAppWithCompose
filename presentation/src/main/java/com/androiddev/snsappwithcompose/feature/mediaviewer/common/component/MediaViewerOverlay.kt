package com.androiddev.snsappwithcompose.feature.mediaviewer.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.androiddev.domain.model.MediaPost

@Composable
fun MediaViewerOverlay(
    currentPage:Int,
    totalCount:Int,
    mediaPost: MediaPost,
    onBackClick:()->Unit = {},
    onMoreClick:()-> Unit = {},
    navigateToPost:()->Unit = {},
    showSeekBar: Boolean = false ,
    duration: Long = 0 ,
    currentPosition: Long = 0 ,
    onSeek: (Long) -> Unit = {}

) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        MediaViewerTopOverlay(
            modifier = Modifier.align(Alignment.TopCenter),
            currentPage = currentPage,
            totalCount = totalCount,
            onBackClick = onBackClick,
            onMoreClick = onMoreClick
        )

        MediaViewerBottomOverlay(
            modifier = Modifier.align(Alignment.BottomCenter),
            mediaPost = mediaPost,
            navigateToPost = navigateToPost,
            showSeekBar = showSeekBar,
            duration = duration,
            currentPosition = currentPosition,
            onSeek = onSeek
        )
    }

}