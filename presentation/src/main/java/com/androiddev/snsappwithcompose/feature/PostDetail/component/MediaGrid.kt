package com.androiddev.snsappwithcompose.feature.PostDetail.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.androiddev.domain.model.Media

@Composable
fun MediaGrid(mediaList: List<Media>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // 정사각형
    ) {
        when (mediaList.size) {
            1 -> SingleMedia(mediaList[0])
            2 -> TwoMedia(mediaList)
            3 -> ThreeMedia(mediaList)
            else -> FourPlusMedia(mediaList)
        }
    }
}