package com.androiddev.snsappwithcompose.feature.notification.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Message
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NotificationItem(
   // icon: ImageVector,
 //   message: String,
 //   time: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically   // 메시지 텍스트와만 정렬됨
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 12.dp)
            )

            Text(
                text = "누군가 당신의 게시물에 댓글을 달았습니다\n\"왜이러는거야...\"",
            )
        }

        // 시간은 아래 줄에 별도로 배치
        Text(
            text = "방금전",
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp)
        )
    }
    /**Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 아이콘
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 12.dp)
        )

        // 메시지 + 시간
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "누군가 당신의 게시물에 댓글을 달았습니다\n왜이러는거야...",
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "방금전",
                modifier = Modifier.align(Alignment.End)   // 오른쪽 끝 정렬
            )
        }
    }**/
}