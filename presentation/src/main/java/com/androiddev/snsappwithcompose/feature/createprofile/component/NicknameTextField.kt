package com.androiddev.snsappwithcompose.feature.createprofile.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.androiddev.snsappwithcompose.R

@Composable
fun NicknameTextField(
    modifier: Modifier,
    text:()->String,
    focusManager: FocusManager,
    onDone: KeyboardActionScope.() -> Unit = { focusManager.clearFocus() },
    onTextChange: (String) -> Unit = {},
    hint: String = "",
    isTyping: () -> Boolean,
    isNicknameValid: ()-> Boolean,
    isNicknameChecking: ()-> Boolean
) {
    val context = LocalContext.current
    OutlinedTextField(modifier = modifier,
        value = text() ,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color.Black
        ),
        onValueChange = {
            onTextChange(it)
        },
        placeholder = { Text(hint) },
        supportingText = {
                Row(
                    modifier = Modifier
                        //.fillMaxWidth()
                        .height(IntrinsicSize.Max)
                        .padding(vertical = 5.dp),
                ) {
                    if(!isTyping()) {
                        Image(
                            painterResource(
                                if(text().length<2) R.drawable.notification
                                else if(isNicknameChecking()) R.drawable.heart
                                else if(isNicknameValid()) R.drawable.checked_circle
                                else R.drawable.wrong
                            ),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if(isTyping()) getString(context,R.string.typing)
                        else if(text().length<2) getString(context,R.string.nickname_condition)
                        else if (isNicknameChecking()) getString(context,R.string.validating)
                        else if(isNicknameValid()) getString(context,R.string.valid_nickname)
                        else getString(context,R.string.invalid_nickname),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
        },

    )

}