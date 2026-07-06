package com.androiddev.snsappwithcompose.feature.userprofile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.androiddev.snsappwithcompose.feature.userprofile.UserContent
import com.androiddev.snsappwithcompose.feature.userprofile.UserProfileViewModel

@Composable
fun MediaGridTab(
    type: UserContent,
    viewModel: UserProfileViewModel
) {
    val pagingItems =
        viewModel.getMediaPosts(type)
            .collectAsLazyPagingItems()


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

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text("데이터를 불러오지 못했습니다.")

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            pagingItems.retry()
                        }
                    ) {
                        Text("다시 시도")
                    }
                }
            }

        }
        else -> {
            if (pagingItems.itemCount == 0) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(text = "게시물이 없습니다.", modifier = Modifier.padding(top = 100.dp))
                }


            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(1.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    items(
                        count = pagingItems.itemCount
                    ) { index ->

                        pagingItems[index]?.let { post ->

                            MediaPostGridItem(
                                post = post,
                                onClick = {}
                            )
                        }
                    }


                    item(
                        span = {
                            GridItemSpan(maxLineSpan)
                        }
                    ) {

                        when (pagingItems.loadState.append) {

                            is LoadState.Loading -> {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            is LoadState.Error -> {

                                Box(
                                    modifier = Modifier.fillMaxWidth(),
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

                            else -> Unit
                        }
                    }
                }

            }
        }
    }

}