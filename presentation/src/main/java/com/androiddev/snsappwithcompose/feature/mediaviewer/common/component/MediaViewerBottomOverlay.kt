package com.androiddev.snsappwithcompose.feature.mediaviewer.common.component


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.ThumbUpAlt
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androiddev.domain.model.MediaPost
import com.androiddev.domain.util.elapsedTime
import com.androiddev.snsappwithcompose.feature.mediaviewer.videoviewer.component.VideoSeekBar
import androidx.compose.foundation.layout.Spacer

@Composable
fun MediaViewerBottomOverlay(
    modifier:Modifier = Modifier,
    navigateToPost:() -> Unit = {},
    mediaPost: MediaPost,
    showSeekBar:Boolean = false,
    seekBarEnabled:Boolean = false,


    duration:Long = 0L,

    currentPosition:Long = 0L,

    onSeek:(Long)->Unit = {}
) {


    Box(
        modifier = modifier
            .fillMaxWidth()

    ){
        OverlayGradient(
            position = GradientPosition.BOTTOM,
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .align(Alignment.BottomCenter)

        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(
                    horizontal = 14.dp,
                    vertical = 24.dp
                )
                .animateContentSize(
                    animationSpec = tween(220)
                )
        ) {
            Text(text = mediaPost.nickname,color = Color.White)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = "${elapsedTime(mediaPost.date)} · ${mediaPost.distance}km",color = Color.LightGray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(11.dp))
            Text(
                text = mediaPost.text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable{ navigateToPost() },
                color = Color.White
            )
            Spacer(modifier = Modifier.height(30.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.ThumbUpAlt,
                    contentDescription = null,
                    tint = Color.White.copy(0.8f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("${mediaPost.likecount}",color = Color.LightGray)

                Spacer(modifier = Modifier.width(17.dp))

                Icon(
                    imageVector = Icons.Outlined.Comment,
                    contentDescription = null,
                    tint = Color.White.copy(0.8f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("${mediaPost.commentCount}",color = Color.LightGray)


            }


            AnimatedVisibility(
                visible = seekBarEnabled,
                enter = fadeIn(animationSpec = tween(80)),
                exit = fadeOut(animationSpec = tween(80)),
            ){
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${formatTime(currentPosition)}/${formatTime(duration)}",
                            color = Color.White
                        )
                    }


                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            VideoSeekBar(
                currentPosition = currentPosition,
                duration = duration,
                onSeek = { targetPosition ->
                    onSeek(targetPosition)
                },
                showSeekBar = showSeekBar,
                enabled = seekBarEnabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


private fun formatTime(timeMs: Long): String {

    val totalSeconds = timeMs / 1000

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}