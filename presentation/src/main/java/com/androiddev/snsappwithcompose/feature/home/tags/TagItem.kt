package com.androiddev.snsappwithcompose.feature.home.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import com.androiddev.domain.model.Tag
import com.androiddev.snsappwithcompose.R

@Composable
fun TagItem(
    tag: Tag,
    onTagClick:()->Unit,
    onFavoriteClick:()->Unit
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).clickable { onTagClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.85f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "#",
                color = Color.Gray.copy(alpha = 0.6f),
                fontSize = 48.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "#"+tag.tagname,
                    modifier = Modifier.align(Alignment.Start),
                    color = Color.Black,
                    fontSize = 15.sp
                )

                if(tag.tagcount!=null){
                    Text(
                        text = getString(context, R.string.story)+tag.tagcount,
                        modifier = Modifier.align(Alignment.Start),
                        color = Color.LightGray,
                        fontSize = 15.sp

                    )
                }

            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if(tag.isliked == 1) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterVertically).clickable { onFavoriteClick() },
                tint = Color.Black
            )
        }
    }


}