package com.androiddev.snsappwithcompose.feature.upload_post.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun UploadRecordIcon(
    recorded: Boolean,
    onClick: ()->Unit
) {
    Box {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Mic Icon",
        )

        // 뱃지는 오른쪽 위로 정렬
        if(recorded) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .align(Alignment.TopEnd).offset(x = 3.dp, y = (-4).dp)
                    .background(Color.White, shape = CircleShape)
            )
        }

    }

}