package com.androiddev.snsappwithcompose.feature.mediaviewer.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.androiddev.domain.model.MediaPost

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
        ImageViewerTopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            currentPage = currentPage,
            totalCount = totalCount,
            onBackClick = onBackClick,
            onMoreClick = onMoreClick
        )

        ImageViewerBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            imagePost = imagePost,
            navigateToPost = navigateToPost
        )
    }

}