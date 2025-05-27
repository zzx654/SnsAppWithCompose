package com.androiddev.snsappwithcompose.util

import com.androiddev.snsappwithcompose.navigation.components.Screen

sealed class UiEvent {
    data class ShowToast(val message: String): UiEvent()
    data class navigate(val screen: Screen): UiEvent()
}