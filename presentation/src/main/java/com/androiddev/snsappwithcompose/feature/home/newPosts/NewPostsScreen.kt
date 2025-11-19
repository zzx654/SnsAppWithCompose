package com.androiddev.snsappwithcompose.feature.home.newPosts

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.common.base.component.BasePostsScreen

@Composable
fun NewPostsScreen(
    navController: NavController,
    viewModel: NewPostsViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
) {
    BasePostsScreen(
        navController = navController,
        viewModel = viewModel
    )
}