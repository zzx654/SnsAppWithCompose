package com.androiddev.snsappwithcompose.feature.mediaviewer.videoviewer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward5
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun VideoController(
    isPlaying:Boolean,
    onPlayClick:()->Unit = {},
    onForwardClick: () -> Unit = {},
    onBackwardClick: () -> Unit = {},
    modifier: Modifier = Modifier
){
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(
                20.dp,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ){

            CircleControlButton(
                onClick = onBackwardClick,
                icon = Icons.Default.Replay5,
                modifier = Modifier.size(52.dp)
            )

            CircleControlButton(
                onClick = onPlayClick,
                icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                modifier = Modifier.size(68.dp),
                iconSize = 42.dp
            )

            CircleControlButton(
                onClick = onForwardClick,
                icon = Icons.Default.Forward5,
                modifier = Modifier.size(52.dp)
            )

        }

    }



}

@Composable
fun CircleControlButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconSize: Dp = 22.dp
) {
    Box(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.35f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}
