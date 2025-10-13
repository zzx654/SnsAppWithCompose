package com.androiddev.snsappwithcompose.home.tags

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.components.LoadingProgressIndicator

import com.androiddev.snsappwithcompose.components.SearchTextField

@Composable
fun TagScreen(
    navController: NavController,
    viewModel: TagViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val state = viewModel.getTagsState.value

    LazyColumn(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            if(state.isLoading) {
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


        if(!state.isLoading) {
            item{
                Spacer(modifier = Modifier.height(10.dp))
                if(state.searchedTags.isNotEmpty()) {
                    Row(modifier = Modifier.fillMaxWidth(0.85f)) {
                        Text(fontWeight = FontWeight.Bold,text = "검색된 태그",color = Color.Black)
                    }
                }
            }
            items(state.searchedTags) { tag ->
                TagItem(tag)
                HorizontalDivider(modifier = Modifier.fillMaxWidth(0.85f),thickness = 1.dp,color = Color.LightGray)
            }

            item {

                if(state.searchedTags.isEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(0.85f)) {
                        Text(fontWeight = FontWeight.Bold,text = getString(context, R.string.favorite_tag),color = Color.Black)
                    }

                }
            }
            item {
                if(state.searchedTags.isEmpty()&&state.favoriteTags.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = getString(context, R.string.add_tag),
                            color = Color.LightGray
                        )
                    }
                }
            }
            if(state.searchedTags.isEmpty()) {
                items(state.favoriteTags) { tag ->
                    TagItem(tag)
                    HorizontalDivider(modifier = Modifier.fillMaxWidth(0.85f),thickness = 1.dp,color = Color.LightGray)
                }
            }

            item{
                if(state.searchedTags.isEmpty()) {
                    Row(modifier = Modifier.fillMaxWidth(0.85f)) {
                        Text(fontWeight = FontWeight.Bold,text = getString(context,R.string.popular_tag),color = Color.Black)
                    }
                }
            }
            if(state.searchedTags.isEmpty()) {
                items(state.popularTags) { tag ->
                    TagItem(tag)
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