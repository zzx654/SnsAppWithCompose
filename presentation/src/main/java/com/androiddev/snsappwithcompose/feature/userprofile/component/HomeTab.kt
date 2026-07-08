package com.androiddev.snsappwithcompose.feature.userprofile.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.paging.compose.collectAsLazyPagingItems
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.component.paging.PagingAppendState
import com.androiddev.snsappwithcompose.common.component.paging.PagingScreen
import com.androiddev.snsappwithcompose.common.mapper.toUiState
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_AUDIO
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_IMAGE
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_VIDEO
import com.androiddev.snsappwithcompose.feature.home.component.PostPrevItem
import com.androiddev.snsappwithcompose.feature.home.component.PostPreviewItem
import com.androiddev.snsappwithcompose.feature.userprofile.UserProfileViewModel

@Composable
fun HomeTab(
    viewModel: UserProfileViewModel
) {
    val pagingItems =
        viewModel.homePosts.collectAsLazyPagingItems()

    PagingScreen(
        refreshState = pagingItems.loadState.refresh,
        itemCount = pagingItems.itemCount,
        onRetry = { pagingItems.retry() },
        emptyMessage = getString(LocalContext.current, R.string.post_not_exist)

    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()){

            items(
                count = pagingItems.itemCount
            ) { index ->

                pagingItems[index]?.let { post ->

                    PostPreviewItem(
                        uiState = post.toUiState()
                    )

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 2.dp,
                        color = Color.LightGray
                    )
                }
            }

            item {

                PagingAppendState(
                    loadState = pagingItems.loadState.append,
                    onRetry = pagingItems::retry
                )
            }
        }

    }

}