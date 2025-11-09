package com.androiddev.snsappwithcompose.feature.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.androiddev.snsappwithcompose.ui.theme.TextFieldBackground

@Composable
fun SignInTextField(
    modifier: Modifier, text:()->String,
    focusManager: FocusManager,
    onDone: KeyboardActionScope.() -> Unit = { focusManager.clearFocus() },
    onTextChange: (String) -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    hint: String = ""
) {

    BasicTextField(
        modifier = modifier,
        value = text(),
        onValueChange = {
            onTextChange(it)
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = onDone),
        visualTransformation =
        if(keyboardType == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .background(color = TextFieldBackground.copy(alpha = 0.2f), shape = RoundedCornerShape(size = 16.dp))
                    .padding(horizontal = 16.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Box( contentAlignment = Alignment.CenterStart) {
                    if(text().isEmpty()) {
                        Text(
                            text = hint,
                            //fontSize = 18.sp,
                            color = Color.Gray,
                        )
                    }
                    innerTextField()
                }



            }
        }
    )
}