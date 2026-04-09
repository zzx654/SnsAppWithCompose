package com.androiddev.snsappwithcompose.feature.home.user

import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.component.SearchTextField
import com.androiddev.snsappwithcompose.common.state.UiEvent
import com.androiddev.snsappwithcompose.common.util.Constants.PAGE_SIZE
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.state.CommentLikeState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun SearchUserScreen(
    navController: NavController,
    viewModel: UserViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
) {
    val context = LocalContext.current
    val state = viewModel.getUsersState.value
    val listState = rememberLazyListState()
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                val totalItemsCount = listState.layoutInfo.totalItemsCount
                if (
                    totalItemsCount>=PAGE_SIZE &&
                    lastVisibleIndex != null &&
                    lastVisibleIndex >= totalItemsCount-1 &&
                    !state.isLoading &&
                    !state.endReached
                ) {
                    viewModel.onEvent(UserEvent.LoadNext)
                }
            }
    }
    LaunchedEffect(Unit) {

        viewModel.eventFlow.collectLatest { event ->
            when(event){
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).also {
                        it.setGravity(Gravity.BOTTOM, 0, 130)
                        it.show()
                    }
                }
                is UiEvent.navigate -> {
                    navController.navigate(event.screen)
                }
                is UiEvent.popBackStack -> {
                    navController.popBackStack()
                }
                else -> null
            }
        }
    }
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

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(state.users.size) { index ->
                val followUserStatus = viewModel.followUserStatusMap[state.users[index].userId]?: false
                UserItem(
                    user = state.users[index],
                    following = followUserStatus,
                    onUserClick = { viewModel.onEvent(UserEvent.SelectUser(state.users[index].userId))},
                    onFollowClick = { viewModel.onEvent(UserEvent.ToggleFollowUser(state.users[index].userId))}
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
            }
            if(state.isLoading && state.users.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color.Black.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}