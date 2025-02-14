package com.androiddev.snsappwithcompose.auth.components

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import com.androiddev.snsappwithcompose.R


@Composable
fun BirthTextField(
    onClick: () -> Unit,
    birth: () -> Int?
) {

    val context = LocalContext.current
    Box {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = birthText(context,birth()),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray
            ),
            onValueChange = {
            },
            placeholder = { Text(getString(context,R.string.birth_year)) }
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable(onClick = onClick),
        )
    }


}
fun birthText(context:Context,birth:Int?):String
=if(birth == null) "" else "${getString(context,R.string.birth_year)} (${birth})"

