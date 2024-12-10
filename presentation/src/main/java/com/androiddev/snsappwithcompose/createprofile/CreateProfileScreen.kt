package com.androiddev.snsappwithcompose.createprofile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.navigation.NavController

@Composable
fun CreateProfileScreen(navController: NavController) {
    Box(
      modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "createProfile",modifier = Modifier.align(Alignment.Center))
    }

}
