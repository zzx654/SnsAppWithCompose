package com.androiddev.snsappwithcompose.feature.userprofile.component

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import com.androiddev.domain.model.User
import com.androiddev.snsappwithcompose.feature.PostDetail.ProfileImage

@Composable
fun UserProfileHeader(
    //user:User,
    context: Context,
    imageLoader: ImageLoader
    ) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth(),
            //.padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileImage(
            modifier = Modifier.size(73.dp),
            profileImage = null,
            //profileImage = user.profileImage?:"",
            gender = "",
            //gender = user.gender?:"",
            context = context,
            imageLoader = imageLoader
        )

        Spacer(modifier = Modifier.width(13.dp))

        Column {
            Text(
                text = "이원상",
                //user.nickname?:"",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(13.dp))
            Text(
                text = "팔로워 3 . 게시물 4개",
                //text = "팔로워 ${user.followerCount} · 게시물 ${user.numberOfPosts}개 ",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}