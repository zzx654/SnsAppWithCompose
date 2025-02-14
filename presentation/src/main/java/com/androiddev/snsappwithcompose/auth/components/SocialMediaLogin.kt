package com.androiddev.snsappwithcompose.auth.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SocialMediaLogIn(
    @DrawableRes icon: Int,
    onClick: () -> Unit,
) {
    Image(
        modifier = Modifier
            .size(40.dp)
            .shadow(
                elevation = 10.dp,
                shape = CircleShape,
                clip = true
            )
            .clickable { onClick()}
        ,
        contentScale = ContentScale.Crop,
        painter = painterResource(id = icon),
        contentDescription = null
    )
}