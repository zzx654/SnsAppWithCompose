package com.androiddev.snsappwithcompose.feature.home.postlist.tagposts

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.androiddev.snsappwithcompose.common.component.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.common.component.TabPager
import com.androiddev.snsappwithcompose.feature.home.tags.TagEvent
import com.androiddev.snsappwithcompose.feature.home.tags.TagViewModel
import com.androiddev.snsappwithcompose.common.navigation.component.Screen


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TagPostScreen(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
    viewModel: TagViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
) {
    var args = navBackStackEntry.toRoute<Screen.TagPostsScreen>()
    val tag = viewModel.getTagById(args.tagId)

    val newTagPostsScreen: @Composable () -> Unit = remember(args.tagId) {
        { TagRecentPostsScreen(navController, args.tagId) }
    }
    val popularTagPostsScreen: @Composable () -> Unit = remember(args.tagId) {
        { TagPopularPostsScreen(navController, args.tagId) }
    }

    val tabs = listOf("새로운", "인기")
    val pages = listOf(newTagPostsScreen, popularTagPostsScreen)

    Scaffold(
        topBar = {
            CenterAlignedTopBar(
                title  = tag?.tagname ?: "",
                onBackClick = { navController.popBackStack() },
                rightAction = {
                    IconButton(onClick = {
                        tag?.tagid?.let { viewModel.onEvent(TagEvent.ToggleFavoriteTag(it)) }
                    }) {
                        Icon(
                            imageVector = if (tag?.isliked == 1) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle Favorite"
                        )
                    }
                }

            )

        }
    ) {
        // 게시물 리스트 등
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            TabPager(
                tabs = tabs,
                pages = pages
            )
        }
    }
}