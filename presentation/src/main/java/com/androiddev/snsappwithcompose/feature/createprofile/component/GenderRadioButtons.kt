package com.androiddev.snsappwithcompose.feature.createprofile.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun GenderRadioButtons(
    items: List<String>,
    selectedValue: () -> String,
    onSelect: (String) -> Unit
) {
    items.forEach { item ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(end = 16.dp)
        ) {
            RadioButton(
                selected = item == selectedValue(),
                onClick = { onSelect(item)},
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.Black,
                    unselectedColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = item)
        }
    }
}
