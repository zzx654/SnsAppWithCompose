package com.androiddev.snsappwithcompose.feature.mediaviewer.videoviewer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
@Composable
fun VideoSeekBar(
    currentPosition: Long,       // 현재 재생 위치 (밀리초)
    duration: Long,              // 전체 재생 시간 (밀리초)
    onSeek: (Long) -> Unit,      // 탐색 완료 시 비디오 플레이어로 시크 요청할 콜백
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    showSeekBar: Boolean = false
) {
    val initialFraction = if (duration > 0f) currentPosition.toFloat() / duration else 0f



// 내부 탐색을 위한 상태

    var sliderPosition by remember { mutableStateOf(initialFraction) }

    var isDragging by remember { mutableStateOf(false) }

    val trackHeight = if (enabled) 3.dp else 1.5.dp

    // 재생 위치 업데이트 및 showSeekBar=false 시 리셋
    LaunchedEffect(currentPosition, duration,showSeekBar) {

        if (!isDragging) {

            sliderPosition = if (duration > 0f) currentPosition.toFloat() / duration else 0f

        }

    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .then(
                if (enabled) {
                    Modifier
                        .pointerInput(duration) {
                            detectTapGestures(
                                onPress = { offset ->
                                    isDragging = true
                                    val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                                    sliderPosition = fraction
                                    tryAwaitRelease()
                                    isDragging = false
                                    onSeek((sliderPosition * duration).toLong())
                                }
                            )
                        }
                        .pointerInput(duration) {
                            detectHorizontalDragGestures(
                                onDragStart = { isDragging = true },
                                onDragEnd = {
                                    isDragging = false
                                    onSeek((sliderPosition * duration).toLong())
                                },
                                onDragCancel = { isDragging = false },
                                onHorizontalDrag = { change, dragAmount ->
                                    change.consume()
                                    val currentWidth = size.width.toFloat()
                                    if (currentWidth > 0) {
                                        val deltaFraction = dragAmount / currentWidth
                                        sliderPosition = (sliderPosition + deltaFraction).coerceIn(0f, 1f)
                                    }
                                }
                            )
                        }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        // 비활성 트랙 (배경 바)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .background(
                    Color.LightGray.copy(alpha = 0.25f),
                    RoundedCornerShape(trackHeight / 2)
                )
        )

        // 활성 트랙 (진행 바 - showSeekBar가 true일 때만 렌더링)
        //if (showSeekBar && sliderPosition > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = sliderPosition)
                    .height(trackHeight)
                    .background(
                        Color.White,
                        RoundedCornerShape(trackHeight / 2)
                    )
            )
       // }

        // 탐색용 썸네일 (드래그 활성화 시만)
        val thumbMaxTravel = maxWidth - 12.dp
        if (enabled) {
            Box(
                modifier = Modifier
                    .offset(x = thumbMaxTravel * sliderPosition)
                    .size(12.dp)
                    .background(Color.White, CircleShape)
            )
        }
    }
}

