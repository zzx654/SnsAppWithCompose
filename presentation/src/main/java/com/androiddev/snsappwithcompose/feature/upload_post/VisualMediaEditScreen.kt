package com.androiddev.snsappwithcompose.feature.upload_post

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.common.component.VisualMediaContent
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.util.rememberMediaPicker

@Composable
fun VisualMediaEditScreen(
    navController: NavController,
    viewModel: UploadPostViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel())
{
    val launchMediaPicker = rememberMediaPicker { uriList ->
        viewModel.onEvent(UploadPostEvent.AddMedia(uriList))
    }

    val context = LocalContext.current

    VisualMediaContent(
        media = viewModel.selectedMediaItems,
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
