package com.androiddev.snsappwithcompose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun <T>RadioChipButtons(
    items: List<T>,
    selectedValue: () -> Int,
    onSelect: (T) -> Unit
) {

    LazyRow {
        items(items) { distance ->

            Row(
                modifier = Modifier
                    .padding(
                        all = 8.dp,
                    ),
            ) {
                Text(
                    text = "${distance}km",
                    style = typography.body1.merge(),
                    color = Color.White,
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
                                Color.LightGray
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
