package com.androiddev.snsappwithcompose.feature.home.nearposts
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.common.component.RadioChipButtons
import com.androiddev.snsappwithcompose.common.base.component.BasePostsScreen
import com.androiddev.snsappwithcompose.feature.home.events.GetNearPostsEvent

@Composable
fun NearPostsScreen(
    navController: NavController,
    viewModel: NearPostsViewModel = hiltViewModel()
) {
    BasePostsScreen(
        navController = navController,
        viewModel = viewModel,
        additionalHeader = {
            RadioChipButtons(
                modifier = Modifier.background(Color.White),
                items = listOf(5, 10, 15, 20, 25, 50, 75, 100),
                startPadding = 8,
                selectedValue = viewModel.distance.value ,
                onSelect = {
                    viewModel.onEvent(GetNearPostsEvent.SetDistance(it))
                },
                label = { item ->
                    "${item}km"

                }
            )
        },
        isNearPostsScreen = true
    )
}