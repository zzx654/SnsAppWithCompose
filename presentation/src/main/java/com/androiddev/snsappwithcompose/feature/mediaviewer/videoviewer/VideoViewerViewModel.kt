package com.androiddev.snsappwithcompose.feature.mediaviewer.videoviewer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.androiddev.domain.model.MediaPost
import com.androiddev.snsappwithcompose.feature.mediaviewer.MediaViewerArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoViewerViewModel @Inject constructor(
    val playerState: VideoPlayerState
): ViewModel() {

    override fun onCleared() {
        super.onCleared()
        playerState.release()
    }
}