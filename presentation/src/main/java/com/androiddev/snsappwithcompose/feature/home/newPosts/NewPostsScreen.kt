package com.androiddev.snsappwithcompose.feature.home.newPosts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.androiddev.snsappwithcompose.common.base.component.BasePostsScreen
import com.androiddev.snsappwithcompose.common.base.component.BasePostssScreen
import kotlinx.coroutines.flow.filter

@Composable
fun NewPostsScreen(
    navController: NavController,
    viewModel: NewPostsViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    onLoaded: ()-> Unit
) {
    var isInitialLoadComplete by remember { mutableStateOf(false) }
    val pagingItems = viewModel.newPosts.collectAsLazyPagingItems()
    LaunchedEffect(pagingItems) {
        snapshotFlow { pagingItems.loadState.refresh }
            .filter { it is LoadState.NotLoading }
            .collect {

                if (!isInitialLoadComplete && pagingItems.itemCount > 0) {
                    isInitialLoadComplete = true
                    onLoaded()
                }
            }


    }
    BasePostssScreen(
        navController = navController,
        viewModel = viewModel,
        pagingItems = pagingItems
    )
}