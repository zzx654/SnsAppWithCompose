package com.androiddev.snsappwithcompose.feature.PostDetail.audio

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AudioPlayer(
    modifier: Modifier = Modifier,
    viewModel: AudioViewModel,
    url: String?
) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.progress.collectAsState()

    url?.let {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            // 검정색 원 (프로그램 중앙에 위치, CircularProgressIndicator 위에 겹침)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(width = 1.dp, color = Color.Black, shape = CircleShape)
            )

            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(52.dp),
                color = androidx.compose.ui.graphics.Color.Black,  // 프로그레스 색 검정
                strokeWidth = 4.dp
            )

            IconButton(onClick = {
                viewModel.toggle()
            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = androidx.compose.ui.graphics.Color.Black // 원이 검정이라 아이콘은 흰색 추천
                )
            }
        }
    }
}