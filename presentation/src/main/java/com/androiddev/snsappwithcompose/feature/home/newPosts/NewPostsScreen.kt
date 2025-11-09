package com.androiddev.snsappwithcompose.feature.home.newPosts

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.common.base.component.BasePostsScreen

@Composable
fun NewPostsScreen(
    navController: NavController,
    viewModel: NewPostsViewModel = hiltViewModel()
) {
    BasePostsScreen(
        navController = navController,
        viewModel = viewModel
    )
}