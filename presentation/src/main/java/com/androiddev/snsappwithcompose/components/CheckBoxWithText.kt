package com.androiddev.snsappwithcompose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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