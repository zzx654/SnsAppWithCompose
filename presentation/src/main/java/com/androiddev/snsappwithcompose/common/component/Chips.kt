package com.androiddev.snsappwithcompose.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Chips(
    modifier: Modifier = Modifier,
    chip: @Composable (data: String, index: Int) -> Unit,
    list: List<String> = emptyList(),
) {
    FlowRow(
        modifier = modifier,
            /**.drawWithContent {
                drawContent()
                drawLine(
                    Color.Gray.copy(alpha = .6f),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 4.dp.toPx()
                )
            },**/
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        list.forEachIndexed{ index,item ->
            chip(item,index)

        }

    }
    
}