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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.androiddev.domain.util.Constants.PAGE_SIZE
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.component.SearchTextField
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.component.paging.PagingListContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource

@Composable
fun SearchUserScreen(
    navController: NavController,
    viewModel: UserViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
) {
    val context = LocalContext.current
    val userStateMap by viewModel.userStateMap.collectAsStateWithLifecycle()
    val searchedUserItems = viewModel.searchedUsersPagingData.collectAsLazyPagingItems()

    val searchQuery by viewModel.nicknameSearchQuery.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {

        viewModel.eventFlow.collectLatest { event ->
            when(event){
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).also {
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
            text = { searchQuery },
            onTextChange = { viewModel.onEvent(UserEvent.TypeNickname(it)) },
            hint = stringResource(R.string.searchuser_hint)
        )

        PagingListContent(
            items = searchedUserItems,
            keyExtractor = { user -> user.userId},
            isInitialState = searchQuery.isBlank(),
            itemContent = { user ->
                val updatedUser = userStateMap[user.userId] ?: user
                UserItem(
                    user = updatedUser,
                    onUserClick = { viewModel.onEvent(UserEvent.SelectUser(user.userId))},
                    onFollowClick = { viewModel.onEvent(UserEvent.ToggleFollowUser(user))}
                )
            },
            dividerContent = { item ->
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.LightGray.copy(alpha = 0.8f)
                )

            }
        )
    }
}