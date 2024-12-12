package com.androiddev.snsappwithcompose.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import com.androiddev.snsappwithcompose.ui.theme.InitProgressBarColor

@Composable
fun LoadingProgressIndicator(isLoading: ()->Boolean) {
    if(isLoading()) {
        CircularProgressIndicator(color = InitProgressBarColor)
    }
}
