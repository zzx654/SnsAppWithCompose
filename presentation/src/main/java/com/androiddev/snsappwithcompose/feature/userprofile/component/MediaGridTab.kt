package com.androiddev.snsappwithcompose.feature.userprofile.component


import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Composable

import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import androidx.paging.compose.collectAsLazyPagingItems
import com.androiddev.domain.model.MediaPost
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.component.paging.DefaultEmptyView
import com.androiddev.snsappwithcompose.common.component.paging.PagingGridContent
import com.androiddev.snsappwithcompose.feature.userprofile.UserContent
import com.androiddev.snsappwithcompose.feature.userprofile.UserProfileViewModel

@Composable
fun MediaGridTab(
    type: UserContent,
    viewModel: UserProfileViewModel,
    canRefresh: Boolean,
    onMediaClick: (List<MediaPost>, Int) -> Unit,
    refreshUserInfo: () -> Unit = {}
) {
    val pagingItems = viewModel.getMediaPosts(type).collectAsLazyPagingItems()
    val context = LocalContext.current

    PagingGridContent(
        items = pagingItems,
        keyExtractor = { post -> post.url }, // 도메인 모델의 unique id
        columns = GridCells.Fixed(2),
        emptyContent = {
            DefaultEmptyView(
                getString(
                    context,
                    if (type == UserContent.IMAGE) R.string.image_not_exist else R.string.video_not_exist
                )
            )
        },
        onRefreshExtra = refreshUserInfo,
        itemContent = { post, index ->
            MediaPostGridItem(
                post = post,
                onClick = {
                    onMediaClick(
                        pagingItems.itemSnapshotList.items,
                        index
                    )
                }
            )
        },
        canRefresh = canRefresh
    )
}