package com.androiddev.snsappwithcompose.feature.home.postlist.tagposts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.androiddev.domain.model.PostListType
import com.androiddev.snsappwithcompose.common.base.BaseScreen
import com.androiddev.snsappwithcompose.common.component.paging.PagingListContent
import com.androiddev.snsappwithcompose.common.mapper.toUiState
import com.androiddev.snsappwithcompose.feature.home.component.PostPreviewItemm
import com.androiddev.snsappwithcompose.feature.home.postlist.recentposts.RecentPostsViewModel
import com.androiddev.snsappwithcompose.feature.postlist.PostListViewModel

@Composable
fun TagRecentPostsScreen(
    navController: NavController,
    tagId:Int,
    viewModel: RecentPostsViewModel = hiltViewModel()
) {
    val postItems =
        viewModel.pagingDataStream.collectAsLazyPagingItems()


    BaseScreen(
        viewModel = viewModel,
        navController = navController,
    ) {

        PagingListContent(
            items = postItems,
            keyExtractor = { post -> post.postId },
            verticalArrangement = Arrangement.spacedBy(6.dp),
            itemContent = { post ->
                PostPreviewItemm(
                    uiState = post.toUiState(),
                    modifier = Modifier.clickable{ viewModel.onClickPostItem(post.postId)}
                )
            }

        )
    }
}