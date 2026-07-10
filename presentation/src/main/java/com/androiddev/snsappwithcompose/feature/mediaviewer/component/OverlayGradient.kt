package com.androiddev.snsappwithcompose.feature.mediaviewer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class GradientPosition {
    TOP,
    BOTTOM
}

@Composable
fun OverlayGradient(
    position: GradientPosition,
    modifier: Modifier = Modifier
) {
    val colors =
        when(position) {
            GradientPosition.TOP ->
                listOf(
                    Color.Black.copy(alpha = 0.65f),
                    Color.Transparent
                )

            GradientPosition.BOTTOM ->
                listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.75f)
                )
        }

    Box(
        modifier = modifier.background(
            Brush.verticalGradient(colors)
        )
    )

}