package com.androiddev.snsappwithcompose.auth.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.androiddev.snsappwithcompose.R

@Composable
fun CheckPassword(pwdChecked: ()-> Boolean) {
    Column (
        modifier = Modifier.fillMaxSize()
    ) {

        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max)) {
            Image(
                painterResource(if(pwdChecked()) R.drawable.checked_circle else R.drawable.unchecked_circle ),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxHeight()
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(R.string.password_rule),modifier = Modifier.fillMaxHeight() )
        }
    }
}