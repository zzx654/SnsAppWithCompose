package com.androiddev.snsappwithcompose.feature.home.nearposts
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.androiddev.snsappwithcompose.common.component.RadioChipButtons
import com.androiddev.snsappwithcompose.common.base.component.BasePostsScreen
import com.androiddev.snsappwithcompose.common.base.component.BasePostssScreen
import com.androiddev.snsappwithcompose.feature.home.events.GetNearPostsEvent

@Composable
fun NearPostsScreen(
    navController: NavController,
    viewModel: NearPostsViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.nearPosts.collectAsLazyPagingItems()
    val distance by viewModel.distance.collectAsStateWithLifecycle()
    BasePostssScreen(
        navController = navController,
        viewModel = viewModel,
        pagingItems = pagingItems,
        additionalHeader = {
            RadioChipButtons(
                modifier = Modifier.background(Color.White),
                items = listOf(5, 10, 15, 20, 25, 50, 75, 100),
                startPadding = 8,
                selectedValue = distance ,
                onSelect = {
                    viewModel.setDistance(it)
                },
                label = { item ->
                    "${item}km"

                }
            )
        },
        isNearPostsScreen = true
    )
}