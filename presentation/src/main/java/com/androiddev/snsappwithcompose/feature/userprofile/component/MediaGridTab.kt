package com.androiddev.snsappwithcompose.feature.userprofile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.paging.compose.collectAsLazyPagingItems
import com.androiddev.domain.model.MediaPost
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.component.paging.PagingAppendState
import com.androiddev.snsappwithcompose.common.component.paging.PagingScreen
import com.androiddev.snsappwithcompose.feature.userprofile.UserContent
import com.androiddev.snsappwithcompose.feature.userprofile.UserProfileViewModel

@Composable
fun MediaGridTab(
    type: UserContent,
    viewModel: UserProfileViewModel,
    canRefresh: Boolean = true,
    onMediaClick: (List<MediaPost>, Int) -> Unit,
    refreshUserInfo: () -> Unit = {}
) {
    val pagingItems = viewModel.getMediaPosts(type).collectAsLazyPagingItems()
    val context = LocalContext.current
    val emptyMessage = remember(type) {
        getString(context, if (type == UserContent.IMAGE) R.string.image_not_exist else R.string.video_not_exist)
    }

    PagingScreen(
        pagingItems = pagingItems,
        emptyMessage = emptyMessage,
        canRefresh = canRefresh,
        onRefresh = refreshUserInfo
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(1.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(count = pagingItems.itemCount) { index ->
                pagingItems[index]?.let { post ->
                    MediaPostGridItem(
                        post = post,
                        onClick = {
                            onMediaClick(
                                pagingItems.itemSnapshotList.items,
                                index
                            )
                        }
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                PagingAppendState(
                    loadState = pagingItems.loadState.append,
                    onRetry = pagingItems::retry
                )
            }
        }
    }
}