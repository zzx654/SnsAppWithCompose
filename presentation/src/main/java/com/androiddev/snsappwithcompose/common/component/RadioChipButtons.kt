package com.androiddev.snsappwithcompose.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun <T>RadioChipButtons(
    items: List<T>,
    selectedValue: () -> Int,
    onSelect: (T) -> Unit
) {

    Box(modifier = Modifier.background(Color.White)) {
        LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
            items(items) { distance ->

                Row(
                    modifier = Modifier
                        .padding(
                            all = 8.dp,
                        ),
                ) {
                    Text(
                        text = "${distance}km",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if(distance == selectedValue())Color.White else Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier
                            .clip(
                                shape = RoundedCornerShape(
                                    size = 12.dp,
                                ),
                            )
                            .clickable {
                                onSelect(distance)
                            }
                            .background(
                                if (distance == selectedValue()) {
                                    Color.Black
                                } else {
                                    Color.LightGray.copy(alpha = 0.4f)
                                }
                            )
                            .padding(
                                vertical = 12.dp,
                                horizontal = 16.dp,
                            ),
                    )
                }
            }
        }
    }

}
