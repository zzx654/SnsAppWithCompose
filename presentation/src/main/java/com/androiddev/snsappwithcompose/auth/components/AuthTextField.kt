package com.androiddev.snsappwithcompose.auth.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun AuthTextField(modifier: Modifier, text:()->String, onTextChange: (String) -> Unit, keyboardType: KeyboardType, hint: String = "") {

    OutlinedTextField(modifier = modifier,
        value = text() ,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray
        ),
        visualTransformation =
        if(keyboardType == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None,
        onValueChange = {
            onTextChange(it)
        },
        placeholder = { Text(hint) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    )
}