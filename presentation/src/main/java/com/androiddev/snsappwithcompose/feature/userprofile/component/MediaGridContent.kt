package com.androiddev.snsappwithcompose.feature.userprofile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import com.androiddev.domain.model.MediaPost
import com.androiddev.domain.model.MediaType
import com.androiddev.snsappwithcompose.BuildConfig


fun LazyListScope.MediaGridContent(
    posts: LazyPagingItems<MediaPost>,
    columns: Int = 2,
    //onPostClick: (Long) -> Unit
) {




    items(
        count = (posts.itemCount + columns - 1) / columns
    ) { rowIndex ->

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            repeat(columns) { columnIndex ->

                val index = rowIndex * columns + columnIndex

                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    if (index < posts.itemCount) {


                        posts[index]?.let { mediaPost ->
                            MediaPostGridItem(
                                post = mediaPost,
                                onClick = {

                                }
                            )

                        }


                    } else {


                        EmptyItem()
                    }
                }
            }
        }
    }
    item {
        Spacer(
            modifier = Modifier.height(300.dp)
        )
    }

    when {

        posts.loadState.append is LoadState.Loading -> {

            item {

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()

                }
            }
        }

        posts.loadState.append is LoadState.Error -> {

            item {

                Text("에러 발생")

            }
        }
    }
}
@Composable
fun EmptyItem(

) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.LightGray)
            .clickable {
                //onClick()
            }
    )
}
@Composable
fun MediaPostGridItem(
    post: MediaPost,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(if(post.type == MediaType.IMAGE.name)1f else 2f/3f)
            .background(Color.LightGray)
            .clickable {
                //onClick()
            }
    ) {
        AsyncImage(
            model = BuildConfig.BASE_URL + post.previewUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop

        )



    }
}