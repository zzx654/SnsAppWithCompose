package com.androiddev.snsappwithcompose.feature.userprofile.component

import android.content.Context
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import com.androiddev.domain.model.User
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.util.shimmerEffect
import com.androiddev.snsappwithcompose.feature.PostDetail.ProfileImage

@Composable
fun UserProfileHeader(
    modifier: Modifier = Modifier,
    user: User?,
    imageLoader: ImageLoader
) {
    val context = LocalContext.current
    val isLoading = user == null

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {


        ProfileImage(
            modifier = Modifier.size(72.dp),
            profileImage = user?.profileImage,
            gender = user?.gender ?: "",
            context = context,
            imageLoader = imageLoader
        )

        Spacer(modifier = Modifier.width(13.dp))

        Column {


            if (isLoading) {
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .width(120.dp)
                        .shimmerEffect()
                )
            } else {
                Text(
                    text = user?.nickname ?: "",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(13.dp))


            if (isLoading) {
                Box(
                    modifier = Modifier
                        .height(13.dp)
                        .width(180.dp)
                        .shimmerEffect()
                )
            } else {
                Text(
                    text = stringResource(
                        R.string.user_profile_stats,
                        user?.followerCount ?: 0,
                        user?.postCount ?: 0),
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            //Spacer(modifier = Modifier.height(100.dp))
        }
    }
}