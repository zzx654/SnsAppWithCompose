package com.androiddev.snsappwithcompose.home.tags.tagposts

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.components.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.components.TabPager
import com.androiddev.snsappwithcompose.home.PlaceholderScreen
import com.androiddev.snsappwithcompose.home.nearposts.NearPostsScreen
import com.androiddev.snsappwithcompose.home.newPosts.NewPostsScreen
import com.androiddev.snsappwithcompose.home.tags.TagEvent
import com.androiddev.snsappwithcompose.home.tags.TagScreen
import com.androiddev.snsappwithcompose.home.tags.TagViewModel
import com.androiddev.snsappwithcompose.home.tags.tagposts.populartagposts.PopularTagPostsScreen
import com.androiddev.snsappwithcompose.navigation.components.Screen

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagPostScreen(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
    viewModel: TagViewModel = hiltViewModel()
) {
    var args = navBackStackEntry.toRoute<Screen.TagPostsScreen>()
    val tag = viewModel.getTagById(args.tagId)
    val tabs = listOf("새로운", "인기")
    val pages = listOf<@Composable () -> Unit>(
        { PlaceholderScreen("새로운") },
        { PopularTagPostsScreen(navController,args.tagId) },
    )

    Scaffold(
        topBar = {
            CenterAlignedTopBar(
                title  = tag?.tagname ?: "",
                onBackClick = { navController.popBackStack() },
                actions = {
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