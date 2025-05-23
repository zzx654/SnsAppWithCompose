package com.androiddev.snsappwithcompose.home.nearposts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.components.LoadingProgressIndicator
import com.androiddev.snsappwithcompose.components.PostPrevItems
import com.androiddev.snsappwithcompose.components.RadioChipButtons

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NearPostsScreen(
    navController: NavController,
    viewModel: NearPostsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(true) {
    }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.getPostState.value.isRefreshing,
        onRefresh = {
            viewModel.onEvent(GetNearPostsEvent.RefreshNearPosts)
        })
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray.copy(alpha = 0.6f)),
    ) {

        LoadingProgressIndicator (modifier = Modifier.align(Alignment.Center)){
            viewModel.getPostState.value.isLoading&&viewModel.getPostState.value.posts.isEmpty()
        }

        if(viewModel.locationPermissionGranted.value) {
            Column(modifier = Modifier.fillMaxSize()) {

                RadioChipButtons(
                    items = listOf(5,10,15,20,25,50,75,100),
                    selectedValue = { viewModel.distance.value },
                    onSelect = {
                        viewModel.onEvent(GetNearPostsEvent.SetDistance(it))
                    }
                )
                Spacer(modifier = Modifier.height(1.dp))
                PostPrevItems(
                    isLoading = {viewModel.getPostState.value.isLoading},
                    endReached = {viewModel.getPostState.value.endReached},
                    posts = { viewModel.getPostState.value.posts },
                    loadNextPosts = { viewModel.onEvent(GetNearPostsEvent.LoadNextPosts) },
                    pullRefreshState = pullRefreshState,
                    onPostClick = { postid -> viewModel.onEvent(GetNearPostsEvent.SelectPost(postid))}
                )
            }
        }
        else {
          Text(
              text = getString(context,R.string.locationpermission_needed),
              modifier = Modifier.align(Alignment.Center))
        }
        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = viewModel.getPostState.value.isRefreshing,
            state = pullRefreshState
        )
    }
}