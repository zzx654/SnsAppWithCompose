package com.androiddev.snsappwithcompose.home.nearposts

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.upload_post.UploadPostEvent
import com.androiddev.snsappwithcompose.util.Screen
import com.androiddev.snsappwithcompose.util.checkPermissions
import com.androiddev.snsappwithcompose.util.fetchLocation
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NearPostsScreen(
    navController: NavController,
    viewModel: NearPostsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    LaunchedEffect(true) {
        checkPermissions(
            context = context,
            permissions = arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
            onGranted = {
                fetchLocation(fusedLocationClient) { latitude,longitude ->
                    viewModel.onEvent(GetNearPostsEvent.RefreshNearPosts(latitude!!,longitude!!))

                }
            },
            onUnGranted = {
                //권한이 허용되지않았다는 값을 줘야함
            }
        )
    }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshing.value,
        onRefresh = {
            viewModel.refreshPosts()
        })
    Box(
        modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState).verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        PullRefreshIndicator(refreshing = viewModel.isRefreshing.value, state = pullRefreshState)
        //Column {
            //Text(
               // text = "near",
             //   modifier = Modifier.clickable{ navController.navigate(Screen.PostDetailScreen("포스트디테일로"))}
           // )
         //   Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "${viewModel.num.value}",
                modifier = Modifier.clickable{viewModel.p()}
            )
       // }

    }

}