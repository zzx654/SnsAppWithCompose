package com.androiddev.snsappwithcompose.feature.mediaviewer.imageviewer.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.androiddev.domain.model.MediaPost
import com.androiddev.snsappwithcompose.feature.mediaviewer.common.component.MediaViewerBottomOverlay
import com.androiddev.snsappwithcompose.feature.mediaviewer.common.component.MediaViewerTopOverlay

@Composable
fun ImageViewerOverlay(
    currentPage:Int,
    totalCount:Int,
    imagePost:MediaPost,
    onBackClick:()->Unit = {},
    onMoreClick:()-> Unit = {},
    navigateToPost:()->Unit = {}
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
            imagePost = imagePost,
            navigateToPost = navigateToPost
        )
    }

}