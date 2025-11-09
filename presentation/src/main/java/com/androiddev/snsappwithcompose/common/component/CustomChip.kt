package com.androiddev.snsappwithcompose.common.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.androiddev.snsappwithcompose.ui.theme.Black

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomChip(
    backgroundColor: Color = Color.Gray,
    text: String,
    onChipClicked: ()->Unit = {},
    onDeleteClick: (()->Unit)? = null,
    border: Boolean = false

) {

    Chip(
        shape = RoundedCornerShape(50.dp),
        onClick = { onChipClicked()},
        border = if(border) BorderStroke(0.dp, Black.copy(alpha = 0.9f)) else null,


    ){
        Text(
            text = text,
            modifier = Modifier.weight(1f, fill = false),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        onDeleteClick?.let {
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        onDeleteClick()
                    }
                    .background(Color.Black.copy(alpha = .4f))
                    .size(16.dp)
                    .padding(2.dp),
                imageVector = Icons.Filled.Close,
                tint = Color(0xFFE0E0E0),
                contentDescription = null
            )
        }

    }

}