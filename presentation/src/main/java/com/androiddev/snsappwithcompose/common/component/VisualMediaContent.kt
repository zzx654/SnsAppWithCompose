package com.androiddev.snsappwithcompose.common.component

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import com.androiddev.domain.model.MediaType
import com.androiddev.snsappwithcompose.BuildConfig
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.feature.upload_post.component.MediaItem
import com.androiddev.snsappwithcompose.feature.upload_post.component.MediaItemView

@Composable
fun VisualMediaContent(
    media:List<MediaItem>,
    isEditMode:Boolean = false,
    onDeleteClick:(MediaItem)->Unit = {},
    onComplete:()->Unit,
    launchMediaPicker:()->Unit = {},
    onVideoClick:(String)->Unit
) {

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                CenterAlignedTopBar(
                    title = getString(LocalContext.current, R.string.media),
                    onBackClick = if(!isEditMode) onComplete else null,
                    rightAction = if (isEditMode) {
                        {
                            IconButton(onClick = onComplete) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null
                                )
                            }
                        }
                    } else {
                        {}
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
                    items = media
                ) { item ->
                    MediaItemView(
                        item = item,
                        isEditMode = isEditMode,
                        onClick = {
                            if(item.type== MediaType.VIDEO) {
                                val source = item.uri?.toString()
                                    ?: (BuildConfig.BASE_URL + item.remotePath)

                                val encoded = Uri.encode(source)
                                onVideoClick(encoded)
                                //navController.navigate(
                               //     Screen.VideoPlayerScreen(
                                 //       encodedUri = encoded,
                                 //   ))

                            }

                        },
                        onDelete = { item ->
                            onDeleteClick(item)
                            //viewModel.onEvent(UploadPostEvent.DeleteMedia(item))

                        }
                    )

                }
                item {
                    if(isEditMode) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                                .clickable { launchMediaPicker() },
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            text = getString(LocalContext.current, R.string.button_text_add_media),
                            color = Color.Black
                        )
                    }

                }

            }


        }

    }

}