package com.androiddev.snsappwithcompose.feature.upload_post.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun CheckBoxWithText(
    text: String,
    checked: ()->Boolean,
    onCheckedChange:(Boolean)->Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked(),
            colors = CheckboxDefaults.colors(
                checkedColor = Color.Black
                , uncheckedColor = Color.Black
                , checkmarkColor = Color.White
            ),
            onCheckedChange = {
                onCheckedChange(it)
            }
        )
        Text(text = text)
    }
}