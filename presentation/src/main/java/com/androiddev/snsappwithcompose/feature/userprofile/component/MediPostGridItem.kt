package com.androiddev.snsappwithcompose.feature.userprofile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.androiddev.domain.model.MediaPost
import com.androiddev.domain.model.MediaType
import com.androiddev.snsappwithcompose.BuildConfig



@Composable
fun MediaPostGridItem(
    post: MediaPost,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(if(post.type == MediaType.IMAGE.name)1f else 2f/3f)
            .background(Color.LightGray)
            .clickable {
                onClick()
            }
    ) {
        AsyncImage(
            model = BuildConfig.BASE_URL + post.previewUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop

        )



    }
}