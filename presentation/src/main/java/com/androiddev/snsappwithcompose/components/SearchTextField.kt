package com.androiddev.snsappwithcompose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchTextField(
    modifier: Modifier,
    text: () -> String,
    onTextChange: (String) -> Unit = {},
    hint: String = ""
) {

    BasicTextField(
        //modifier = modifier,
        value = text(),
        onValueChange = {
            onTextChange(it)
        },
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    //.padding(horizontal = 64.dp)
                    .fillMaxWidth()
                    .background(color = Color(0xFFD2F3F2), shape = RoundedCornerShape(size = 16.dp))
                    .padding(all = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "",
                    tint = Color.DarkGray,
                )
                Spacer(modifier = Modifier.width(width = 8.dp))
                innerTextField()
            }
        }
    )
}