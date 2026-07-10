package com.androiddev.snsappwithcompose.feature.mediaviewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.androiddev.domain.model.MediaPost
import com.androiddev.snsappwithcompose.feature.mediaviewer.component.ImagePager
import com.androiddev.snsappwithcompose.feature.mediaviewer.component.ImageViewerBottomBar
import com.androiddev.snsappwithcompose.feature.mediaviewer.component.ImageViewerTopBar
import com.androiddev.snsappwithcompose.feature.mediaviewer.component.ZoomableContainer

@Composable
fun ImageViewerScreen(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry
) {



    val previousEntry = remember(navController) {
        navController.previousBackStackEntry
    }

    val imagePosts =
        previousEntry
            ?.savedStateHandle
            ?.get<List<MediaPost>>(MediaViewerArgs.MEDIA)

    val index =
        previousEntry
            ?.savedStateHandle
            ?.get<Int>(MediaViewerArgs.CLICKED_INDEX)
    if (imagePosts == null || index == null) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }
    val pagerState = rememberPagerState(
        initialPage = index,
        pageCount = { imagePosts.size }
    )


    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        ZoomableContainer(
            modifier = Modifier.fillMaxSize()
        ) {
            ImagePager(
                images = imagePosts,
                pagerState = pagerState,
                modifier = Modifier.fillMaxSize()
            )
        }

        ImageViewerTopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            currentPage = pagerState.currentPage,
            totalCount = imagePosts.size,
            onBackClick = {
                navController.popBackStack()
            }
        )
        ImageViewerBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            navigateToPost = {},
            imagePost =imagePosts[pagerState.currentPage],
        )
    }

}