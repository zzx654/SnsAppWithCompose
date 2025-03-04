package com.androiddev.snsappwithcompose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
fun SearchTextField(
    modifier: Modifier,
    text: () -> String,
    onTextChange: (String) -> Unit = {},
    hint: String = ""
) {

    BasicTextField(
        value = text(),
        onValueChange = {
            onTextChange(it)
        },

        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = TextFieldBackground.copy(alpha = 0.1f), shape = RoundedCornerShape(size = 16.dp))
                    .padding(all = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "",
                    tint = Color.DarkGray,
                )
                Spacer(modifier = Modifier.width(width = 8.dp))
                Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.CenterStart) {
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