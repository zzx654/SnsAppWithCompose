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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androiddev.domain.model.Post
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.upload_post.UploadPostEvent

@Composable
fun PostPrevItem(
) {
    Column(
        modifier = Modifier.fillMaxWidth().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Chips(
            modifier = Modifier.fillMaxWidth(0.9f).padding(top = 8.dp),
            list = listOf("강아지","고양이","캐럿","안드로이드","포모스","탄핵","선동","지령","북한","간첩"),
            chip = { data: String, index: Int ->
                CustomChip(
                    backgroundColor = Color.Gray,
                    text = data,
                )
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(0.9f).height(IntrinsicSize.Min).padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,

        ) {
            Text(text = "가나다라마바사아 자차카타파하 가나다라 마사바사아 자차카타 파하 가나다라 마바사아 자차카타 파하가나다라마바사아 자차카타파하 가나다라 마사바사아 자차카타 파하 가나다라 마바사아 자차카타 파하가나다라마바사아 자차카타파하 가나다라 마사바사아 자차카타 파하 가나다라 마바사아 자차카타 파하가나다라마바사아 자차카타파하 가나다라 마사바사아 자차카타 파하 가나다라 마바사아 자차카타 파하가나다라마바사아 자차카타파하 가나다라 마사바사아 자차카타 파하 가나다라 마바사아 자차카타 파하",
                overflow = TextOverflow.Ellipsis,
                fontSize = 17.sp,
                maxLines = 4,
                modifier = Modifier.weight(0.7f)
            )
            Spacer(modifier = Modifier.weight(0.08f))
            Box(
                modifier = Modifier
                    .weight(0.25f)
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.person_none),
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
                    text = "+3",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.TopEnd).padding(7.dp)
                )
            }

        }
        Row(
          modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 5.dp)
        ) {
            Text(text = "신입 · 방금전", fontSize = 14.sp,color = Color.Gray.copy(0.8f))
        }
        Spacer(modifier = Modifier.height(2.dp))
        HorizontalDivider(color = Color.Gray.copy(0.7f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 5.dp)
        ) {
        }

    }

}

