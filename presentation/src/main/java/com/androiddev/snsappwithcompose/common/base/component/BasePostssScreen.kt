package com.androiddev.snsappwithcompose.common.base.component

import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import com.androiddev.domain.model.PostPreview
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.viewmodel.BasePagingViewModel
import com.androiddev.snsappwithcompose.common.component.LoadingDialog
import com.androiddev.snsappwithcompose.common.component.paging.PagingAppendState
import com.androiddev.snsappwithcompose.common.component.paging.PagingScreen
import com.androiddev.snsappwithcompose.common.mapper.toUiState
import com.androiddev.snsappwithcompose.common.state.UiEvent
import com.androiddev.snsappwithcompose.feature.home.component.PostPreviewItem
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <VM : BasePagingViewModel> BasePostssScreen(
    navController: NavController,
    pagingItems: LazyPagingItems<PostPreview>,
    viewModel: VM,
    additionalHeader: @Composable (() -> Unit)? = null,
    isNearPostsScreen:Boolean = false
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


    LoadingDialog { viewModel.isLoading.value }
    PagingScreen(
        pagingItems = pagingItems,
        emptyMessage = getString(LocalContext.current, R.string.post_not_exist),
        onRefresh = { viewModel.fetchCurrentLocation() }

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray.copy(alpha = 0.6f)),
        ) {
            if (!viewModel.locationPermissionGranted.value && isNearPostsScreen) {
                Text(
                    text = getString(context, R.string.locationpermission_needed),
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    additionalHeader?.invoke()

                    Spacer(modifier = Modifier.height(1.dp))
                    LazyColumn(modifier = Modifier.fillMaxSize()){
                        items(
                            count = pagingItems.itemCount
                        ) { index ->
                            pagingItems[index]?.let { post ->

                                PostPreviewItem(
                                    modifier = Modifier.clickable { viewModel.onClickPostItem(post.postId)},
                                    uiState = post.toUiState()
                                )

                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 2.dp,
                                    color = Color.LightGray
                                )
                            }
                        }
                        item {

                            PagingAppendState(
                                loadState = pagingItems.loadState.append,
                                onRetry = pagingItems::retry
                            )
                        }

                    }

                }


            }

        }

    }


}