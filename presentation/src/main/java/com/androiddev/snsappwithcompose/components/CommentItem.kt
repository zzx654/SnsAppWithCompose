package com.androiddev.snsappwithcompose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.LocalMinimumTouchTargetEnforcement
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androiddev.domain.model.Comment
import com.androiddev.snsappwithcompose.PostDetail.ProfileImage
import androidx.core.content.ContextCompat.getString
import com.androiddev.snsappwithcompose.R


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommentItem(
    comment: Comment
) {
    val context = LocalContext.current
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProfileImage(comment.profileImage, comment.gender, comment.anonymous,context)

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        comment.nickname,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = comment.elapsedTime,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

            }
            Spacer(modifier = Modifier.height(15.dp))
            Text(text = comment.text,modifier = Modifier.padding(horizontal = 50.dp))
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.padding(horizontal = 50.dp)) {
                Text(
                    text = getString(context, R.string.like)+if(comment.likeCount!=0)" ${comment.likeCount}" else "",
                    fontWeight = if(comment.commentLiked == 1) FontWeight.Bold else FontWeight.Normal,
                    color = if(comment.commentLiked == 1) Color.Black else Color.Gray.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = getString(context, R.string.write_reply))
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            IconButton(
                onClick = { /* TODO: 메뉴 클릭 처리 */ },
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options"
                )
            }
        }

    }
}