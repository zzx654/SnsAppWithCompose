package com.androiddev.snsappwithcompose.auth.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BottomButton(
    buttonText:String,
    activeButton: ()->Boolean,
    onClick: ()->Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)) {
        Button(   colors = ButtonDefaults.buttonColors(
            containerColor = if(activeButton()) Color.Black else Color.Gray
        ),modifier = Modifier.fillMaxSize(),shape = RoundedCornerShape(0.dp),onClick = {if(activeButton()){onClick()} }) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}