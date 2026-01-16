package com.androiddev.snsappwithcompose.feature.home.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import coil3.imageLoader
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.androiddev.domain.model.User
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.feature.PostDetail.ProfileImage

@Composable
fun UserItem(
    user: User,
    following:Boolean,
    onUserClick:()->Unit,
    onFollowClick:()->Unit
) {
    val context = LocalContext.current
    val imageLoader = remember {
        context.imageLoader.newBuilder()
            .crossfade(false)
            .logger(DebugLogger())
            .build()
    }
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp).clickable { onUserClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.85f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ProfileImage(
                modifier = Modifier.align(Alignment.CenterVertically).size(46.dp),
                profileImage = user.profileImage,
                gender = user.gender,
                anonymous = false,
                context = context,
                imageLoader = imageLoader
            )
            Column(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = user.nickname,
                    modifier = Modifier.align(Alignment.Start),
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = getString(context,R.string.follower)+" "+ user.followerCount,
                    modifier = Modifier.align(Alignment.Start),
                    color = Color.LightGray,
                    fontSize = 15.sp

                )

            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (following) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterVertically).clickable { onFollowClick() },
                tint = Color.Gray
            )
        }
    }
}