package com.androiddev.snsappwithcompose.home

import android.view.Gravity
import android.widget.Toast
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
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.base.BasePostsViewModel
import com.androiddev.snsappwithcompose.components.LoadingDialog
import com.androiddev.snsappwithcompose.components.LoadingProgressIndicator
import com.androiddev.snsappwithcompose.components.PostPrevItems
import com.androiddev.snsappwithcompose.home.events.GetPostsEvent
import com.androiddev.snsappwithcompose.util.UiEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <VM : BasePostsViewModel> BasePostsScreen(
    navController: NavController,
    viewModel: VM,
    additionalHeader: @Composable (() -> Unit)? = null
) {
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).also {
                        it.setGravity(Gravity.BOTTOM, 0, 130)
                        it.show()
                    }
                }
                is UiEvent.navigate -> {
                    navController.navigate(event.screen)
                }
                else -> null
            }
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.getPostState.value.isRefreshing,
        onRefresh = {
            viewModel.onEvent(GetPostsEvent.Refresh)
        }
    )

    LoadingDialog { viewModel.isLoading.value }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray.copy(alpha = 0.6f)),
    ) {
        LoadingProgressIndicator(modifier = Modifier.align(Alignment.Center)) {
            viewModel.getPostState.value.isLoading && viewModel.getPostState.value.posts.isEmpty()
        }

        if (viewModel.locationPermissionGranted.value) {
            Column(modifier = Modifier.fillMaxSize()) {

                additionalHeader?.invoke()

                Spacer(modifier = Modifier.height(1.dp))

                PostPrevItems(
                    isLoading = { viewModel.getPostState.value.isLoading },
                    endReached = { viewModel.getPostState.value.endReached },
                    posts = { viewModel.getPostState.value.posts },
                    loadNextPosts = { viewModel.onEvent(GetPostsEvent.LoadNext) },
                    pullRefreshState = pullRefreshState,
                    onPostClick = { postId ->
                        viewModel.onEvent(GetPostsEvent.SelectPost(postId))
                    }
                )
            }
        } else {
            Text(
                text = getString(context, R.string.locationpermission_needed),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = viewModel.getPostState.value.isRefreshing,
            state = pullRefreshState
        )
    }
}