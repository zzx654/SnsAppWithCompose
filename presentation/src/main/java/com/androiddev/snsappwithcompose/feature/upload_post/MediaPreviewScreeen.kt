package com.androiddev.snsappwithcompose.feature.upload_post

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.androiddev.domain.model.MediaType
import com.androiddev.snsappwithcompose.BuildConfig
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.component.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.util.rememberMediaPicker
import com.androiddev.snsappwithcompose.feature.home.tags.TagEvent
import com.androiddev.snsappwithcompose.feature.upload_post.component.MediaItem
import com.androiddev.snsappwithcompose.feature.upload_post.component.MediaItemView


/**@Composable
fun MediaPreviewScreen(
    navController:NavController,
    viewModel: UploadPostViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
) {

    val launchMediaPicker = rememberMediaPicker { uriList ->
        viewModel.onEvent(UploadPostEvent.AddMedia(uriList))
    }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                CenterAlignedTopBar(
                    title = getString(LocalContext.current, R.string.media),
                    rightAction = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()

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

                items(
                    items = viewModel.selectedMediaItems
                ) { item ->
                    MediaItemView(
                        item = item,
                        onClick = {
                            if(item.type== MediaType.VIDEO) {
                                val source = item.uri?.toString()
                                    ?: (BuildConfig.BASE_URL + item.remotePath)

                                val encoded = Uri.encode(source)
                                navController.navigate(
                                    Screen.VideoPlayerScreen(
                                        encodedUri = encoded,
                                ))

                            }

                                  },
                        onDelete = { item ->
                            viewModel.onEvent(UploadPostEvent.DeleteMedia(item))

                        }
                    )

                }
               item {
                   Text(
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(14.dp)
                           .clickable { launchMediaPicker() },
                       textAlign = TextAlign.Center,
                       fontWeight = FontWeight.Bold,
                       fontSize = 18.sp,
                       text = getString(context,R.string.button_text_add_media),
                       color = Color.Black
                   )
               }

            }


        }

    }


}**/
