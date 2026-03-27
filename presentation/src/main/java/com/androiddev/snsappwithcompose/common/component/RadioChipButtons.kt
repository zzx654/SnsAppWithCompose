package com.androiddev.snsappwithcompose.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedValue: T,
    onSelect: (T) -> Unit,
    label: (T) -> String
) {


        LazyRow(
            modifier = modifier
                .padding(vertical = 10.dp)
        ) {
            items(items) { item ->

                val isSelected = item == selectedValue

                Text(
                    text = label(item),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = if (isSelected) Color.White else Color.Black,
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isSelected) Color.Black
                            else Color.LightGray.copy(alpha = 0.3f)
                        )
                        .clickable { onSelect(item) }
                        .padding(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        )
                )
            }
        }
    }


