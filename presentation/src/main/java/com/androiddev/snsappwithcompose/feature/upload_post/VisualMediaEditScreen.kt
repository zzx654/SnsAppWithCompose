package com.androiddev.snsappwithcompose.feature.upload_post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.common.component.VisualMediaContent
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.util.MediaItemFactory
import com.androiddev.snsappwithcompose.common.util.rememberMediaPicker
import kotlinx.coroutines.launch

@Composable
fun VisualMediaEditScreen(
    navController: NavController,
    viewModel: UploadPostViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel())
{
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val mediaItemFactory = remember(context) { MediaItemFactory(context) }
    val launchMediaPicker = rememberMediaPicker { uris ->
        scope.launch {
            val mediaItems = mediaItemFactory.createMediaItems(uris)
            viewModel.onEvent(UploadPostEvent.AddMedia(mediaItems))
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    VisualMediaContent(
        media = uiState.selectedMediaItems,
        isEditMode = true,
        onDeleteClick = {
            viewModel.onEvent(UploadPostEvent.DeleteMedia(it))
        },
        onComplete = {
            navController.popBackStack()
        },
        launchMediaPicker = launchMediaPicker,
        onVideoClick = { encoded ->
            navController.navigate(
                Screen.VideoPlayerScreen(
                    encodedUri = encoded,
                ))
        }

    )

}
