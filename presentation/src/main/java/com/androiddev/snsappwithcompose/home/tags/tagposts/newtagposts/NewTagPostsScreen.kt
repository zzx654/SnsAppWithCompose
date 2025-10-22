package com.androiddev.snsappwithcompose.home.tags.tagposts.newtagposts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.home.BasePostsScreen

@Composable
fun NewTagPostsScreen(
    navController: NavController,
    tagId:Int,
    viewModel: NewTagPostsViewModel = hiltViewModel()
) {
    LaunchedEffect(tagId) {
        viewModel.initTagPosts(tagId)

    }
    BasePostsScreen(
        navController = navController,
        viewModel = viewModel
    )
}