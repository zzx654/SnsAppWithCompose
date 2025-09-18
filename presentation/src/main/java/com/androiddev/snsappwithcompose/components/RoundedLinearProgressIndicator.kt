package com.androiddev.snsappwithcompose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RoundedLinearProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    backgroundColor: Color = Color.LightGray
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(color, RoundedCornerShape(4.dp))
        )
    }
}