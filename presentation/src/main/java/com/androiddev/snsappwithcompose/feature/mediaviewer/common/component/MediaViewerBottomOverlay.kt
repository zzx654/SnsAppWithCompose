package com.androiddev.snsappwithcompose.feature.mediaviewer.common.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

@Composable
fun MediaViewerBottomOverlay(
    modifier:Modifier = Modifier,
    navigateToPost:() -> Unit = {},
    imagePost: MediaPost
) {

    Box(
        modifier = modifier
            .fillMaxWidth()

    ){
        OverlayGradient(
            position = GradientPosition.BOTTOM,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
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
        ) {
            Text(text = imagePost.nickname,color = Color.White)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = "${elapsedTime(imagePost.date)} · ${imagePost.distance}km",color = Color.LightGray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(11.dp))
            Text(
                text = imagePost.text,
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
                Text("${imagePost.likecount}",color = Color.LightGray)

                Spacer(modifier = Modifier.width(17.dp))

                Icon(
                    imageVector = Icons.Outlined.Comment,
                    contentDescription = null,
                    tint = Color.White.copy(0.8f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("${imagePost.commentCount}",color = Color.LightGray)


            }


        }
    }

}