package com.androiddev.snsappwithcompose.feature.notification.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androiddev.domain.model.NotificationItem
import com.androiddev.snsappwithcompose.feature.notification.NotificationType

@Composable
fun NotificationItem(
    modifier: Modifier = Modifier,
    notification: NotificationItem,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 15.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onNotificationClick() },
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector =
                if (notification.type == NotificationType.COMMENT || notification.type == NotificationType.REPLY)
                    Icons.Default.ChatBubbleOutline
                else Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier
                .size(28.dp),
                //.padding(end = 10.dp),
            tint = if (notification.isRead) Color.LightGray else Color.Black)

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = notification.content,
                color = if (notification.isRead) Color.LightGray else Color.Black,
                fontSize = 13.sp,
                maxLines = 2,
                lineHeight = 20.sp,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    //.padding(end = 46.dp) // 🔹 elapsedTime 영역 확보
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "",
                //text = notification.elapsedTime,
                modifier = Modifier
                    .align(Alignment.Start),
                    //.padding(top = 4.dp,start = 40.dp),
                color = if (notification.isRead) Color.LightGray else Color.Gray,
                fontSize = 12.sp
            )
        }
    }

}