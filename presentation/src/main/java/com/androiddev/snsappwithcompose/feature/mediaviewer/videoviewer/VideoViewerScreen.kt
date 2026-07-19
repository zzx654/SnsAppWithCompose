package com.androiddev.snsappwithcompose.feature.mediaviewer.videoviewer

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.androiddev.domain.model.MediaPost
import com.androiddev.snsappwithcompose.feature.mediaviewer.MediaViewerArgs
import com.androiddev.snsappwithcompose.feature.mediaviewer.common.component.MediaViewerOverlay
import com.androiddev.snsappwithcompose.feature.mediaviewer.videoviewer.component.VideoController
import com.androiddev.snsappwithcompose.feature.mediaviewer.videoviewer.component.VideoPlayer

@Composable
fun VideoViewerScreen(
    viewModel:VideoViewerViewModel = hiltViewModel(),
    navController:NavController
) {


    val context = LocalContext.current
    val previousEntry = navController.previousBackStackEntry


    val videoPosts =
        previousEntry
            ?.savedStateHandle
            ?.get<List<MediaPost>>(MediaViewerArgs.MEDIA)
            ?: emptyList()

    val index =
        previousEntry
            ?.savedStateHandle
            ?.get<Int>(MediaViewerArgs.CLICKED_INDEX)




    if (videoPosts == null || index == null) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    val playerState = remember {
        VideoPlayerState(context)
    }
    DisposableEffect(Unit) {
        onDispose {
            playerState.release()
        }
    }

    var isPlaying by remember {
        mutableStateOf(true)
    }
    val pagerState = rememberPagerState(
        initialPage = index,
        pageCount = { videoPosts.size }
    )

    LaunchedEffect(
        pagerState.currentPage
    ) {


        isPlaying = true


        playerState.play(
            url = videoPosts[pagerState.currentPage].url
        )
    }

    VerticalPager(
        state = pagerState,
        key = { page -> videoPosts[page].id},
        modifier = Modifier.fillMaxSize()
    ) { page ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    isPlaying = !isPlaying
                    playerState.setPlaying(isPlaying)
                }
        ) {

            if (page == pagerState.currentPage) {
                VideoPlayer(
                    player = playerState.player,
                    modifier = Modifier.fillMaxSize()
                )

                MediaViewerOverlay(
                    currentPage = pagerState.currentPage,
                    totalCount = videoPosts.size,
                    mediaPost = videoPosts[pagerState.currentPage],
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onMoreClick = {
                        // TODO
                    },
                    navigateToPost = {
                        // TODO
                    },
                    showSeekBar = !isPlaying,
                    duration = playerState.duration,
                    currentPosition = playerState.currentPosition,
                    onSeek = { playerState.seekTo(it)}
                )


                AnimatedVisibility(
                    visible = !isPlaying,
                    enter = fadeIn(
                        animationSpec = tween(120)
                    ),
                    exit = fadeOut(
                        animationSpec = tween(120)
                    )
                ) {
                    VideoController(
                        isPlaying = isPlaying,
                        onBackwardClick = {
                            isPlaying = true
                            playerState.skipBackward()
                        },
                        onForwardClick = {
                            isPlaying = true
                            playerState.skipForward()
                        },
                        onPlayClick = {
                            isPlaying = !isPlaying
                            playerState.setPlaying(isPlaying)
                        }
                    )
                }
                }


            if(playerState.isBuffering){

                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center),
                    color = Color.White
                )

            }




        }
    }
}
