package com.androiddev.snsappwithcompose.feature.mediaviewer.imageviewer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewerViewModel @Inject constructor() : ViewModel() {

    var uiState by mutableStateOf(ImageViewerUiState())
        private set

    fun setCurrentPage(page: Int) {
        uiState = uiState.copy(currentPage = page)
    }

    fun toggleOverlay() {
        uiState = uiState.copy(
            showOverlayUi = !uiState.showOverlayUi
        )
    }

    fun showOverlay() {
        uiState = uiState.copy(showOverlayUi = true)
    }

    fun hideOverlay() {
        uiState = uiState.copy(showOverlayUi = false)
    }
}