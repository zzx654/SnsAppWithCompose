package com.androiddev.snsappwithcompose.feature.mediaviewer.videoviewer.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){

            IconButton(
                onClick = onBackwardClick
            ){
                Icon (
                    imageVector =
                        Icons.Default.Replay5,

                    contentDescription = null
                )
            }


            IconButton(
                onClick = onPlayClick
            ){

                Icon(
                    imageVector =
                        if(isPlaying)
                            Icons.Default.Pause
                        else
                            Icons.Default.PlayArrow,

                    contentDescription = null
                )

            }


            IconButton(
                onClick = onForwardClick
            ){
                Icon (
                    imageVector =
                        Icons.Default.Forward5,

                    contentDescription = null
                )
            }

        }

    }



}
