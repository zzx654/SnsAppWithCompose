package com.androiddev.snsappwithcompose.common.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.androiddev.snsappwithcompose.ui.theme.InitProgressBarColor

@Composable
fun LoadingProgressIndicator(modifier:Modifier = Modifier,isLoading: ()->Boolean) {
    if(isLoading()) {
        CircularProgressIndicator(modifier = modifier, color = InitProgressBarColor)
    }
}
