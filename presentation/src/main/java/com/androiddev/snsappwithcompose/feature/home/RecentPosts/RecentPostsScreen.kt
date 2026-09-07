package com.androiddev.snsappwithcompose.feature.home.RecentPosts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.androiddev.domain.model.PostListType
import com.androiddev.snsappwithcompose.common.base.BaseScreen
import com.androiddev.snsappwithcompose.common.component.paging.PagingListContent
import com.androiddev.snsappwithcompose.common.mapper.toUiState
import com.androiddev.snsappwithcompose.feature.home.component.PostPreviewItemm
import com.androiddev.snsappwithcompose.feature.postlist.PostListViewModel

@Composable
fun RecentPostsScreen(
    viewModel: PostListViewModel = hiltViewModel(),
    navController: NavController,
    onLoaded: ()-> Unit
) {

    var isInitialLoadComplete by remember { mutableStateOf(false) }
    val postItems =
        viewModel.pagingDataStream.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.setListType(PostListType.Recent)
    }
    LaunchedEffect(postItems.loadState.refresh) {
        val refreshState = postItems.loadState.refresh

        // 최초 로딩 완료, 아직 onLoaded가 실행되지 않았다면 호출
        if (refreshState is LoadState.NotLoading && !isInitialLoadComplete) {
            isInitialLoadComplete = true
            onLoaded()
        }
    }
    BaseScreen(
        viewModel = viewModel,
        navController = navController,
    ) {

        PagingListContent(
            items = postItems,
            keyExtractor = { post -> post.postId},
            verticalArrangement = Arrangement.spacedBy(6.dp),
            itemContent = { post ->
                PostPreviewItemm(
                    uiState = post.toUiState(),
                    modifier = Modifier.clickable{ viewModel.onClickPostItem(post.postId)}
                )
            },


        )
    }
}