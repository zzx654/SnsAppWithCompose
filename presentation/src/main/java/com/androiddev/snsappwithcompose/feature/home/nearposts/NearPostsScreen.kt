package com.androiddev.snsappwithcompose.feature.home.nearposts
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.common.component.RadioChipButtons
import com.androiddev.snsappwithcompose.common.base.component.BasePostsScreen
import com.androiddev.snsappwithcompose.feature.home.events.GetNearPostsEvent

@Composable
fun NearPostsScreen(
    navController: NavController,
    viewModel: NearPostsViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
) {
    BasePostsScreen(
        navController = navController,
        viewModel = viewModel,
        additionalHeader = {
            RadioChipButtons(
                items = listOf(5,10,15,20,25,50,75,100),
                selectedValue = { viewModel.distance.value },
                onSelect = {
                    viewModel.onEvent(GetNearPostsEvent.SetDistance(it))
                }
            )
        },
        isNearPostsScreen = true
    )
}