package com.androiddev.snsappwithcompose.home.nearposts
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.components.LoadingDialog
import com.androiddev.snsappwithcompose.components.LoadingProgressIndicator
import com.androiddev.snsappwithcompose.components.PostPrevItems
import com.androiddev.snsappwithcompose.components.RadioChipButtons
import com.androiddev.snsappwithcompose.home.BasePostsScreen
import com.androiddev.snsappwithcompose.home.events.GetNearPostsEvent
import com.androiddev.snsappwithcompose.home.events.GetPostsEvent
import com.androiddev.snsappwithcompose.util.UiEvent
import kotlinx.coroutines.flow.collectLatest

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
                items = listOf(5, 10, 15, 20, 25, 50, 75, 100),
                selectedValue = { viewModel.distance.value },
                onSelect = {
                    viewModel.onEvent(GetNearPostsEvent.SetDistance(it))
                }
            )
        }
    )
}