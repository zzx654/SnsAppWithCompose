package com.androiddev.snsappwithcompose.feature.home.tagposts.populartagposts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.common.base.component.BasePostsScreen

@Composable
fun PopularTagPostsScreen(
    navController: NavController,
    tagId:Int,
    viewModel: PopularTagPostsViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
) {
    LaunchedEffect(tagId) {
        viewModel.initTagPosts(tagId)

    }
    BasePostsScreen(
        navController = navController,
        viewModel = viewModel
    )
}