package com.androiddev.snsappwithcompose.feature.Reply

import androidx.compose.foundation.clickable
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
import androidx.core.content.ContextCompat.getString
import coil3.ImageLoader
import com.androiddev.domain.model.Comment
import com.androiddev.snsappwithcompose.feature.PostDetail.ProfileImage
import com.androiddev.snsappwithcompose.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReplyItem(
    comment: Comment,
    //isLiked: Boolean,
    //likeCount: Int,
    imageLoader: ImageLoader,
    onLikeClick: ()->Unit,
    onOptionClick: ()->Unit,

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
                    .padding(horizontal = 47.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProfileImage(
                    profileImage = comment.profileImage,
                    gender = comment.gender,
                    anonymous = comment.anonymousNickname!=null,
                    context = context,
                    imageLoader = imageLoader
                )

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
            Text(text = comment.text,modifier = Modifier.padding(horizontal = 97.dp))
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.padding(horizontal = 97.dp)) {
                Text(
                    text = getString(context, R.string.like),
                    fontWeight = if(comment.commentLiked == 1) FontWeight.Bold else FontWeight.Normal,
                    color = if(comment.commentLiked == 1) Color.Black else Color.Gray.copy(alpha = 0.8f),
                    modifier = Modifier.clickable { onLikeClick() }
                )
                if(comment.likeCount>0) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${comment.likeCount}")
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            IconButton(
                onClick = { onOptionClick() },
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