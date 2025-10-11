package com.androiddev.snsappwithcompose.home.tags

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

import com.androiddev.snsappwithcompose.components.SearchTextField

@Composable
fun TagScreen(
    navController: NavController,
    viewModel: TagViewModel = hiltViewModel()
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            SearchTextField(
                modifier = Modifier.fillMaxWidth(),
                text = { viewModel.tagTextField.value },
                onTextChange = { viewModel.onEvent(TagEvent.TypeTag(it)) },
                hint = "원하는 태그를 검색해 보세요"
            )
        }

    }
}