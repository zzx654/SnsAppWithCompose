package com.androiddev.snsappwithcompose.feature.home.nearbyposts

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.androiddev.domain.model.PostListType
import com.androiddev.snsappwithcompose.common.base.BaseScreen
import com.androiddev.snsappwithcompose.common.component.RadioChipButtons
import com.androiddev.snsappwithcompose.common.component.paging.PagingListContent
import com.androiddev.snsappwithcompose.common.mapper.toUiState
import com.androiddev.snsappwithcompose.feature.home.component.PostPreviewItemm
import com.androiddev.snsappwithcompose.feature.postlist.PostListViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NearbypostsScreen(
    viewModel: PostListViewModel = hiltViewModel(),
    navController: NavController
) {
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ),
    )


    val hasLocationPermission = locationPermissionsState.allPermissionsGranted ||
            locationPermissionsState.revokedPermissions.size < locationPermissionsState.permissions.size

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            viewModel.setListType(PostListType.Nearby(radiusKm = 5))
        }
    }

    val postItems = viewModel.pagingDataStream.collectAsLazyPagingItems()
    val selectedRadius by viewModel.selectedRadius.collectAsStateWithLifecycle()
    BaseScreen(
        viewModel = viewModel,
        navController = navController,
    ) {
        if(hasLocationPermission) {

            PagingListContent(
                items = postItems,
                keyExtractor = { post -> post.postId},
                verticalArrangement = Arrangement.spacedBy(6.dp),
                itemContent = { post ->
                    PostPreviewItemm(
                        uiState = post.toUiState(),
                        modifier = Modifier.clickable{ viewModel.onClickPostItem(post.postId)}
                    )
                              },
                modifier = Modifier
                    .fillMaxSize(),
                additionalHeader =  {
                    RadioChipButtons(
                        modifier = Modifier.background(Color.White),
                        items = listOf(5, 10, 15, 20, 25, 50, 75, 100),
                        startPadding = 8,
                        selectedValue = selectedRadius ,
                        onSelect = {
                            viewModel.setRadius(it) },
                        label = { radius ->
                            "${radius}km"

                        }
                    )
                }

            )

        } else {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "위치권한이 필요합니다",color = Color.Gray,modifier = Modifier.align(Alignment.Center))
            }
        }

    }

}