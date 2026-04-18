package com.androiddev.snsappwithcompose.feature.upload_post

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.androiddev.domain.model.MediaType
import com.androiddev.snsappwithcompose.common.component.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.feature.home.tags.TagEvent
import com.androiddev.snsappwithcompose.feature.upload_post.component.MediaItem
import com.androiddev.snsappwithcompose.feature.upload_post.component.MediaItemView


@Composable
fun MediaPreviewScreen(
    navController:NavController,
    viewModel: UploadPostViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
) {
    println("보여주시;오${viewModel.selectedMediaItems}")

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                CenterAlignedTopBar(
                    title = "미디어",
                    onBackClick = { navController.popBackStack() },
                    rightAction = {
                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null
                            )
                        }
                    }
                )
            }


        }

    ) { paddingValues ->
        Box (
            modifier = Modifier.fillMaxSize().padding(paddingValues)

        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                items(viewModel.selectedMediaItems) { item ->
                    MediaItemView(
                        item = item,
                        onClick = {
                            if(item.type== MediaType.VIDEO) {
                                val encoded = Uri.encode(item.uri.toString())
                                navController.navigate(Screen.VideoPreviewScreen(encoded))

                            }

                                  },
                        onDelete = { item ->
                            viewModel.onEvent(UploadPostEvent.DeleteMedia(item))

                        }
                    )

                }
               item {
                   Text(modifier = Modifier.fillMaxWidth(),text = "추가",color = Color.Black)
               }

            }

        }

    }


}
