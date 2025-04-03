package com.androiddev.snsappwithcompose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.HowToVote
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Textsms
import androidx.compose.material.icons.outlined.ThumbUpAlt
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.util.DebugLogger
import com.androiddev.domain.model.Post
import com.androiddev.snsappwithcompose.BuildConfig
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.elapsedTime

@Composable
fun PostPrevItem(
    post: Post
) {
    Column(
        modifier = Modifier.fillMaxWidth().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        post.tags?.let { tags ->
            Chips(
                modifier = Modifier.fillMaxWidth(0.9f).padding(top = 10.dp),
                list = tags.split('#').map{"#${it}"},
                chip = { data: String, index: Int ->
                    CustomChip(
                        backgroundColor = Color.Gray,
                        text = data,
                    )
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(0.9f).height(IntrinsicSize.Min).padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,

        ) {
            Text(
                text = post.text,
                overflow = TextOverflow.Ellipsis,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 4,
                modifier = Modifier.weight(0.7f)
            )
            Spacer(modifier = Modifier.weight(0.08f))
            post.images?.let { images ->
                images.split(',').let {
                    Box(
                        modifier = Modifier
                            .weight(0.25f)
                            .size(90.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        val imageLoader = LocalContext.current.imageLoader.newBuilder()
                            .logger(DebugLogger())
                            .build()
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(BuildConfig.BASE_URL+ it[0])
                                    .build(),
                                imageLoader = imageLoader,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop,
                                contentDescription = null
                            )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(90.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.4f))
                        )
                        Text(
                            text = "+${it.size}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.TopEnd).padding(7.dp)
                        )
                    }
                }

            }


        }
        Row(
          modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 5.dp)
        ) {
            var nickname = post.nickname
            post.anonymous?.let {
                if(it!="NONE")
                nickname = "익명[${it}]"
            }
            Text(text = "${nickname} · ${elapsedTime(post.date)}", fontSize = 14.sp,color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(7.dp))
        HorizontalDivider(color = Color.Gray.copy(0.7f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = Color.DarkGray.copy(0.8f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("0km",color = Color.DarkGray)
            Spacer(modifier = Modifier.width(9.dp))
            Icon(
                imageVector = Icons.Outlined.Comment,
                contentDescription = null,
                tint = Color.DarkGray.copy(0.8f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("0",color = Color.DarkGray.copy(0.8f))
            Spacer(modifier = Modifier.width(9.dp))
            Icon(
                imageVector = Icons.Outlined.ThumbUpAlt,
                contentDescription = null,
                tint = Color.DarkGray.copy(0.8f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("0",color = Color.DarkGray.copy(0.8f))
            Spacer(modifier = Modifier.width(9.dp))
            Icon(
                imageVector = Icons.Outlined.HowToVote,
                contentDescription = null,
                tint = Color.DarkGray.copy(0.8f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("0",color = Color.DarkGray.copy(0.8f))
            Spacer(modifier = Modifier.width(9.dp))
            Icon(
                imageVector = Icons.Outlined.Mic,
                contentDescription = null,
                tint = Color.DarkGray.copy(0.8f)
            )
        }
    }
}

