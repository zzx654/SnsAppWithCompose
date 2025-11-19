package com.androiddev.snsappwithcompose.feature.home.tagposts.newtagposts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.common.base.component.BasePostsScreen

@Composable
fun NewTagPostsScreen(
    navController: NavController,
    tagId:Int,
    viewModel: NewTagPostsViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
) {
    LaunchedEffect(tagId) {
        viewModel.initTagPosts(tagId)

    }
    BasePostsScreen(
        navController = navController,
        viewModel = viewModel
    )
}