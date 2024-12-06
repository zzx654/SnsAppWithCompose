package com.androiddev.snsappwithcompose.util

sealed class UiEvent {
    data class ShowToast(val message: String): UiEvent()
    data class navigate(val screen: Screen): UiEvent()
}