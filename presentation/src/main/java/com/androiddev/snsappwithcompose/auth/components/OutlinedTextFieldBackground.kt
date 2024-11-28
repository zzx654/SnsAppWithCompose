package com.androiddev.snsappwithcompose.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OutlinedTextFieldBackground(
    color: Color,
    content: @Composable () -> Unit
) {
    Box {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = color,
                    shape = RoundedCornerShape(4.dp)
                )
        )
        content()
    }

}