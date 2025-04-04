package com.androiddev.snsappwithcompose.home.nearposts


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
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
import com.androiddev.snsappwithcompose.components.PostPrevItem
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
        refreshing = viewModel.isRefreshing.value,
        onRefresh = {
            viewModel.onEvent(GetNearPostsEvent.RefreshNearPosts)
        })
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray.copy(alpha = 0.6f)),
    ) {

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

                LazyColumn(
                    modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)
                ) {
                    items(viewModel.posts.value) {

                        PostPrevItem(it)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                }
            }
        }
        else {
            Text(
                text = getString(context,R.string.locationpermission_needed),
                modifier = Modifier.align(Alignment.Center))
        }
        if(viewModel.isLoading.value&&viewModel.posts.value.isEmpty()) {
                CircularProgressIndicator(color = Color.Black,modifier = Modifier.align(Alignment.Center))
        }
        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = viewModel.isRefreshing.value,
            state = pullRefreshState
        )


    }

}