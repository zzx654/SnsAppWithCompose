package com.androiddev.snsappwithcompose.auth.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.androiddev.snsappwithcompose.R

@Composable
fun AuthNumberTextField(
    isNumberReceived: ()->Boolean,
    number: ()->String,
    onNumberChange: (String) -> Unit,
    limitTime: ()->Int,
    hint: String,
    inCorrect:()->Boolean
) {
    if(isNumberReceived()) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = number() ,
            singleLine = true,
            isError = inCorrect(),
            supportingText = {
                if(inCorrect()) {
                    Text(text = stringResource(R.string.incorrect_authcode),color = Color.Red)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                errorBorderColor = Color.Red,
            ),
            trailingIcon = { Text(modifier = Modifier.padding(horizontal = 20.dp),text = timertoString(limitTime()),color = Color.Red) },
            placeholder = { Text(hint) },
            onValueChange = { if(it.length<=6) onNumberChange(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
    }
}

fun timertoString(timer: Int):String {
    val m: String = (timer / 60).toString()
    val s: String
    val sec = (timer % 60)
    s = if (sec < 10)
        "0$sec"
    else
        sec.toString()

    return "$m:$s"
}