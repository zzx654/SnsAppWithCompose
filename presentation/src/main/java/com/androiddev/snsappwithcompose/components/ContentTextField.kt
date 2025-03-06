package com.androiddev.snsappwithcompose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androiddev.snsappwithcompose.ui.theme.TextFieldBackground

@Composable
fun ContentTextField(
    text: () -> String,
    onTextChange: (String) -> Unit = {},
    hint: String = ""
) {
    BasicTextField(
        //modifier = Modifier.height(200.dp),
        value = text(),
        onValueChange = {
            onTextChange(it)
        },
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 360.dp)
                    .background(color = TextFieldBackground.copy(alpha = 0.1f), shape = RoundedCornerShape(size = 16.dp))
                    .padding(all = 16.dp),
            ) {
                Spacer(modifier = Modifier.width(width = 8.dp))
                Box(contentAlignment = Alignment.TopStart) {
                    if(text().isEmpty()) {
                        Text(
                            text = hint,
                            fontSize = 18.sp,
                            color = Color.Gray,
                        )
                    }
                    innerTextField()

                }
            }
        }
    )
}