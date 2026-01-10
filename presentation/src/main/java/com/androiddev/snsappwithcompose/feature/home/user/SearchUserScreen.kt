package com.androiddev.snsappwithcompose.feature.home.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.component.SearchTextField

@Composable
fun SearchUserScreen(
    navController: NavController,
    viewModel: UserViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
) {
    val context = LocalContext.current
    val state = viewModel.getUsersState.value

    Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally) {
        // 상단 고정 검색창
        SearchTextField(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(vertical = 20.dp),
            text = { viewModel.nicknameTextField.value },
            onTextChange = { viewModel.onEvent(UserEvent.TypeNickname(it)) },
            hint = getString(context, R.string.searchtag_hint)
        )

        // LazyColumn: 남은 공간을 채우면서 스크롤 가능
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(state.users.size) { index ->
                if(index >= state.users.size - 1 && !state.endReached && !state.isLoading) {
                    viewModel.onEvent(UserEvent.LoadNext)
                }
                UserItem(
                    user = state.users[index],
                    onUserClick = {},
                    onFollowClick = {}
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
            }
            item {
                if(state.isLoading && state.users.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}