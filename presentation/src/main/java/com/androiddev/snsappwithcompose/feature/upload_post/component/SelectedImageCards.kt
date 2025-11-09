package com.androiddev.snsappwithcompose.feature.upload_post.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.androiddev.snsappwithcompose.BuildConfig
import com.androiddev.snsappwithcompose.ui.theme.Black

@Composable
fun SelectedImageCards(
    selectedImages:()-> List<EditableImage>,
    onDeleteClick:(EditableImage) -> Unit

) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(
                start = 10.dp,
                end = 10.dp,
                bottom = 10.dp
            )
    ) {
        items(selectedImages()) { image ->

            Box(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)

            ) {
                AsyncImage(
                    model = image.uri ?: (BuildConfig.BASE_URL + image.remotePath),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentDescription = null
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    tint = Color.White,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .size(12.dp)
                        .align(Alignment.TopStart)
                        .clickable { onDeleteClick(image)  }
                        .drawBehind {
                            drawCircle(
                                color = Black,
                                radius = this.size.maxDimension
                            )
                        }
                )

            }
        }
    }
}