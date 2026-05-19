package com.androiddev.snsappwithcompose.feature.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.androiddev.domain.model.PostPreview
import com.androiddev.snsappwithcompose.common.base.viewmodel.PostUiState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostPrevItems(
    isLoading:()->Boolean,
    endReached:()->Boolean,
    posts:()->List<PostPreview>,
    uiPosts:List<PostUiState>,
    loadNextPosts:()->Unit,
    pullRefreshState: PullRefreshState,
    onPostClick:(Int)->Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        postPrevItemsContent(
            isLoading = isLoading,
            endReached = endReached,
            posts = posts,
            uiPosts = uiPosts,
            loadNextPosts = loadNextPosts,
            onPostClick = onPostClick
        )


    }
}
fun LazyListScope.postPrevItemsContent(
    isLoading: () -> Boolean,
    endReached: () -> Boolean,
    posts: () -> List<PostPreview>,
    uiPosts:List<PostUiState>,
    loadNextPosts: () -> Unit,
    onPostClick: (Int) -> Unit
) {

    items(posts().size) { index ->

        if (index >= posts().size - 1 && !endReached() && !isLoading()) {
            loadNextPosts()
        }

        PostPrevItem(
            post = posts()[index],
            modifier = Modifier.clickable {
                onPostClick(posts()[index].postId)
            },
            image = uiPosts[index].imageUrls,
            hasVideo = uiPosts[index].hasVideo,
            hasAudio = uiPosts[index].hasAudio,
            displayUserName = uiPosts[index].displayUserName
        )
        //Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = Color.LightGray
        )
    }

    item {
        if (isLoading() && posts().isNotEmpty()) {
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