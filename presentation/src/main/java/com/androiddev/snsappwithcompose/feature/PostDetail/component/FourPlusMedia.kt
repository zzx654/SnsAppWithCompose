package com.androiddev.snsappwithcompose.feature.PostDetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androiddev.domain.model.Media

@Composable
fun FourPlusMedia(list: List<Media>) {

    val displayList = list.take(4)
    val extraCount = list.size - 4

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {

        for (row in displayList.chunked(2)) {

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                row.forEachIndexed { index, item ->

                    val absoluteIndex =
                        displayList.indexOf(item)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    ) {

                        MediaGridItem(
                            item,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (
                            absoluteIndex == 3 &&
                            extraCount > 0
                        ) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(
                                        Color.Black.copy(alpha = 0.5f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+$extraCount",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}