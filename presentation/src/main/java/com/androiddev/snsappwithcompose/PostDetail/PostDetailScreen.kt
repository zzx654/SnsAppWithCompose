package com.androiddev.snsappwithcompose.PostDetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.androiddev.domain.model.PostPreview

@Composable
fun PostDetailScreen(
    post: PostPreview?,
    navController: NavController,
    navBackStackEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    println(post?.tags)
    println(post?.profileImage)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = post!!.text,
            modifier = Modifier.clickable{ navController.popBackStack()}
        )
    }
}