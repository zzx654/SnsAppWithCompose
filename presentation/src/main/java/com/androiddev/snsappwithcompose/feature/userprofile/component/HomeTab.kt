package com.androiddev.snsappwithcompose.feature.userprofile.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.androiddev.snsappwithcompose.common.component.paging.PagingListContent
import com.androiddev.snsappwithcompose.common.mapper.toUiState
import com.androiddev.snsappwithcompose.feature.home.component.PostPreviewItemm
import com.androiddev.snsappwithcompose.feature.userprofile.UserProfileViewModel

@Composable
fun HomeTab(
    viewModel: UserProfileViewModel,
    canRefresh:Boolean
) {
    val postItems =
        viewModel.homePostsDataStream.collectAsLazyPagingItems()
    PagingListContent(
        items = postItems,
        keyExtractor = { post -> post.postId },
        verticalArrangement = Arrangement.spacedBy(6.dp),
        itemContent = { post ->
            PostPreviewItemm(
                uiState = post.toUiState(),
                modifier = Modifier.clickable{ viewModel.onClickPostItem(post.postId)}
            )
        },
        canRefresh = canRefresh

    )

}