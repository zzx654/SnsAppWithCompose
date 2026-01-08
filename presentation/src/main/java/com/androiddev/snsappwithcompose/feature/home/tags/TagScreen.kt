package com.androiddev.snsappwithcompose.feature.home.tags

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.component.LoadingProgressIndicator

import com.androiddev.snsappwithcompose.common.component.SearchTextField
import com.androiddev.snsappwithcompose.common.navigation.component.Screen

@Composable
fun TagScreen(
    navController: NavController,
    viewModel: TagViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
) {

    val context = LocalContext.current
    val state = viewModel.getTagsState.value
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LoadingProgressIndicator(modifier = Modifier.align(Alignment.Center)) {
                            true
                        }
                    }

                }
            }
            item {
                SearchTextField(
                    modifier = Modifier.fillMaxWidth(0.85f).padding(vertical = 20.dp),
                    text = { viewModel.tagTextField.value },
                    onTextChange = { viewModel.onEvent(TagEvent.TypeTag(it)) },
                    hint = getString(context, R.string.searchtag_hint)
                )
            }


            if (!state.isLoading) {
                if (state.searchedTags.isNotEmpty()) { //검색된태그 있을때
                    item {
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(0.85f)) {
                            Text(fontWeight = FontWeight.Bold, text = "검색된 태그", color = Color.Black)
                        }

                    }//검색된태그
                } else { //검색된태그 없ㅇ르때
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(0.85f)) {
                            Text(
                                fontWeight = FontWeight.Bold,
                                text = getString(context, R.string.favorite_tag),
                                color = Color.Black
                            )
                        }


                    }//즐겨찾기 태그
                    item {
                        if (state.favoriteTags.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getString(context, R.string.add_tag),
                                    color = Color.LightGray
                                )
                            }
                        }
                    }//즐겨찾기 추가해라
                    items(state.favoriteTags) { tag ->
                        TagItem(
                            tag = tag,
                            onFavoriteClick = {
                                viewModel.onEvent(
                                    TagEvent.ToggleFavoriteTag(
                                        tag.tagid ?: 0
                                    )
                                )
                            },
                            onTagClick = {
                                navController.navigate(
                                    Screen.TagPostsScreen(
                                        tag.tagid ?: 0
                                    )
                                )
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(0.85f),
                            thickness = 1.dp,
                            color = Color.LightGray
                        )
                    }
                    item {
                        if (state.favoriteTags.isNotEmpty())
                            Spacer(modifier = Modifier.height(20.dp))
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(0.85f)) {
                            Text(
                                fontWeight = FontWeight.Bold,
                                text = getString(context, R.string.popular_tag),
                                color = Color.Black
                            )
                        }
                    }//인기태그
                    items(state.popularTags) { tag ->
                        TagItem(
                            tag = tag,
                            onFavoriteClick = {
                                viewModel.onEvent(
                                    TagEvent.ToggleFavoriteTag(
                                        tag.tagid ?: 0
                                    )
                                )
                            },
                            onTagClick = {
                                navController.navigate(
                                    Screen.TagPostsScreen(
                                        tag.tagid ?: 0
                                    )
                                )
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(0.85f),
                            thickness = 1.dp,
                            color = Color.LightGray
                        )
                    }

                }
                items(state.searchedTags) { tag ->
                    TagItem(
                        tag = tag,
                        onFavoriteClick = {
                            viewModel.onEvent(
                                TagEvent.ToggleFavoriteTag(
                                    tag.tagid ?: 0
                                )
                            )
                        },
                        onTagClick = {
                            navController.navigate(
                                Screen.TagPostsScreen(
                                    tag.tagid ?: 0
                                )
                            )
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        thickness = 1.dp,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}