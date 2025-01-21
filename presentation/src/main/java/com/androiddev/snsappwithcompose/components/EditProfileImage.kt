package com.androiddev.snsappwithcompose.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.ui.theme.profileBorder

@Composable
fun EditProfileImage(
    modifier:Modifier,
    profileBmap:Bitmap?
) {
    Box(
        modifier = modifier
          .width(IntrinsicSize.Min)
          .height(IntrinsicSize.Min)
    ) {
        if(profileBmap == null) {
            Image(
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.person_none),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape) // clip to the circle shape
                    .border(1.dp, profileBorder,CircleShape)
            )
            Image(
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.camera),
                contentDescription = null,
                modifier = Modifier
                    .size(35.dp)
                    .align(Alignment.BottomEnd)
                    .padding(5.dp)
            )
        } else {
            Image(
                contentScale = ContentScale.Crop,
                bitmap = profileBmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape) // clip to the circle shape
                    .border(1.dp, profileBorder,CircleShape)
            )

        }

    }

}