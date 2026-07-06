package com.androiddev.snsappwithcompose.feature.userprofile.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_AUDIO
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_IMAGE
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_VIDEO
import com.androiddev.snsappwithcompose.feature.home.component.PostPrevItem
import com.androiddev.snsappwithcompose.feature.userprofile.UserProfileViewModel

@Composable
fun HomeGridTab(
    viewModel: UserProfileViewModel
) {
    val pagingFlow = remember(viewModel) {
        viewModel.getHomePosts()
    }

    val pagingItems = pagingFlow.collectAsLazyPagingItems()

    when (val refreshState = pagingItems.loadState.refresh) {

        is LoadState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is LoadState.Error -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Button(
                    onClick = {
                        pagingItems.retry()
                    }
                ) {
                    Text("다시 시도")
                }
            }
        }

        else -> {

            if (pagingItems.itemCount == 0) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("게시물이 없습니다.")
                }

            } else {

                LazyColumn(modifier = Modifier.fillMaxSize()){

                    items(
                        count = pagingItems.itemCount
                    ) { index ->

                        pagingItems[index]?.let { post ->

                            PostPrevItem(
                                post = post,
                                image = post.media.filter { it.type == MEDIA_TYPE_IMAGE }
                                    .map { it.url },
                                hasVideo = post.media.any { it.type == MEDIA_TYPE_VIDEO },
                                hasAudio = post.media.any { it.type == MEDIA_TYPE_AUDIO },
                                displayUserName = post.anonymousNickname ?: post.nickname,
                                modifier = Modifier
                            )
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 2.dp,
                                color = Color.LightGray
                            )
                        }
                    }

                    item {

                        when (pagingItems.loadState.append) {

                            is LoadState.Loading -> {
                                CircularProgressIndicator()
                            }

                            is LoadState.Error -> {

                                Button(
                                    onClick = {
                                        pagingItems.retry()
                                    }
                                ) {
                                    Text("다시 시도")
                                }
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
    }

}