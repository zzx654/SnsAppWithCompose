package com.androiddev.snsappwithcompose.feature.home.newPosts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.common.base.component.BasePostsScreen

@Composable
fun NewPostsScreen(
    navController: NavController,
    viewModel: NewPostsViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    onLoaded: ()-> Unit
) {
    var isInitialLoadComplete by remember { mutableStateOf(false) }
    val getPosts = viewModel.getPostState.value
    LaunchedEffect(getPosts.posts) {
        if(getPosts.posts.isNotEmpty()&&!isInitialLoadComplete) {
            isInitialLoadComplete = true
            onLoaded()
        }


    }
    BasePostsScreen(
        navController = navController,
        viewModel = viewModel
    )
}