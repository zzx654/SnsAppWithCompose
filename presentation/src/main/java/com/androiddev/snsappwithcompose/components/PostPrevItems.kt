package com.androiddev.snsappwithcompose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androiddev.domain.model.PostPreview

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostPrevItems(
    isLoading:()->Boolean,
    endReached:()->Boolean,
    posts:()->List<PostPreview>,
    loadNextPosts:()->Unit,
    pullRefreshState: PullRefreshState,
    onPostClick:(Int)->Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {


        items(
            posts().size,
        ) { index ->

            if(index >= posts().size - 1 && !endReached() && !isLoading()) {
                loadNextPosts()
            }
            PostPrevItem(post = posts()[index],modifier = Modifier.clickable{ onPostClick(posts()[index].postId) })
            Spacer(modifier = Modifier.height(4.dp))
        }
        item {
            if(isLoading() && posts().isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

    }
}