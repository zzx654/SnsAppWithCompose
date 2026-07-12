package com.androiddev.snsappwithcompose.feature.mediaviewer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.androiddev.domain.model.MediaPost
import com.androiddev.snsappwithcompose.feature.mediaviewer.component.ImagePager
import com.androiddev.snsappwithcompose.feature.mediaviewer.component.ImageViewerBottomBar
import com.androiddev.snsappwithcompose.feature.mediaviewer.component.ImageViewerOverlay
import com.androiddev.snsappwithcompose.feature.mediaviewer.component.ImageViewerTopBar
import com.androiddev.snsappwithcompose.feature.mediaviewer.component.ZoomableContainer

@Composable
fun ImageViewerScreen(
    viewModel: ImageViewerViewModel=androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
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
    var isZooming by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setCurrentPage(
            pagerState.currentPage
        )
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        ZoomableContainer(
            modifier = Modifier.fillMaxSize()
        ) {
            ImagePager(
                images = imagePosts,
                pagerState = pagerState,
                modifier = Modifier.fillMaxSize(),
                onTap = { viewModel.toggleOverlay() },
                userScrollEnabled = !isZooming,
                onScaleChanged = {
                    isZooming = it > 1f
                }
            )
        }
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = viewModel.uiState.showOverlayUi,
            enter = fadeIn(tween(120)),
            exit = fadeOut(tween(120))
        ) {
            ImageViewerOverlay(
                currentPage = viewModel.uiState.currentPage,
                totalCount = imagePosts.size,
                imagePost = imagePosts[viewModel.uiState.currentPage],
                onBackClick = {
                    navController.popBackStack()
                },
                onMoreClick = {},
                navigateToPost = {}
            )
        }


    }

}