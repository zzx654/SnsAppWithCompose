package com.androiddev.snsappwithcompose.feature.Reply

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.imageLoader
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.androiddev.snsappwithcompose.common.base.BaseScreen
import com.androiddev.snsappwithcompose.common.base.component.BaseScaffold
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.CommentEvent
import com.androiddev.snsappwithcompose.common.viewmodel.CurrentUserViewModel
import com.androiddev.snsappwithcompose.common.component.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.component.CommentInput
import com.androiddev.snsappwithcompose.common.component.SelectorBottomSheetDialog
import com.androiddev.snsappwithcompose.common.component.paging.pagingAppendItems
import com.androiddev.snsappwithcompose.feature.PostDetail.component.BoundCommentRow

@Composable
fun ReplyScreen(
    navController: NavController,
    viewModel: ReplyViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    currentUserViewModel: CurrentUserViewModel
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val imageLoader = remember {
        context.imageLoader.newBuilder()
            .crossfade(false)
            .logger(DebugLogger())
            .build()
    }
    val listState = rememberLazyListState()
    var imeHeigh = remember { mutableStateOf(0) }
    val ime = androidx.compose.foundation.layout.WindowInsets.ime
    val localDensity = LocalDensity.current
    val replyItems =  viewModel.pagingDataStream.collectAsLazyPagingItems()
    val userId by currentUserViewModel.userId.collectAsStateWithLifecycle()

    val newlyAddedComments by viewModel.newlyAddedComments.collectAsStateWithLifecycle()
    val commentStateMap by viewModel.commentStateMap.collectAsStateWithLifecycle()
    val originalCommentUiState by viewModel.originalCommentUiState.collectAsStateWithLifecycle()
    val bottomSheetDialogState by viewModel.bottomSheetDialogState.collectAsStateWithLifecycle()

    val anonymousChecked by viewModel.anonymousChecked.collectAsStateWithLifecycle()
    val newlyAddedIds = remember(newlyAddedComments) {
        newlyAddedComments.map { it.commentId }.toSet()
    }

    val isLoadingCommentAndReplies = originalCommentUiState.isCommentLoading ||
            replyItems.loadState.refresh is LoadState.Loading && replyItems.itemCount == 0
    LaunchedEffect(key1 = Unit) {
        val keyboardFlow = snapshotFlow {
            ime.getBottom(localDensity)
        }

        keyboardFlow.collect { keyboardHeight ->
            val diff = keyboardHeight - imeHeigh.value

            listState.scrollBy(diff.toFloat())

            imeHeigh.value = keyboardHeight
        }
    }
    val pullToRefreshState = rememberPullToRefreshState()
    var isManualRefreshing by remember { mutableStateOf(false) }

    val isRefreshing = isManualRefreshing && (
            replyItems.loadState.refresh is LoadState.Loading || originalCommentUiState.isLoading
            )

    LaunchedEffect(replyItems.loadState.refresh) {
        //pullRefresh 처리
        if (isManualRefreshing &&
            replyItems.loadState.refresh is LoadState.NotLoading && !originalCommentUiState.isLoading
        ) {
            isManualRefreshing = false
        }
    }
    SelectorBottomSheetDialog(bottomSheetDialogState)
    BaseScreen(
        viewModel = viewModel,
        navController = navController,
    ) {
        BaseScaffold(
            modifier = Modifier.fillMaxWidth(),
            focusManager = focusManager,
            scrollState = scrollState,
            topBar = {
                CenterAlignedTopBar(
                    title = originalCommentUiState.commentUiState?.displayUserName?.asString()?:"",
                    onBackClick = { navController.popBackStack() },)

            },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullToRefresh(
                            state = pullToRefreshState,
                            isRefreshing = isRefreshing,
                            onRefresh = {
                                isManualRefreshing = true
                                viewModel.fetchComment()
                                replyItems.refresh()

                            }
                        )
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val comment = originalCommentUiState.comment
                        if(isLoadingCommentAndReplies && !isManualRefreshing) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                    androidx.compose.material3.CircularProgressIndicator(
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            comment?.let {
                                item {
                                    BoundCommentRow(
                                        comment = comment,
                                        commentStateMap = commentStateMap,
                                        userId = userId,
                                        imageLoader = imageLoader,
                                        onCommentEvent = { viewModel.onEvent(it) },
                                        showReplyCount = false
                                    )
                                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(0.2f))
                                }
                            }
                            items(
                                items = newlyAddedComments,
                                key = { it.commentId }
                            ) { comment ->
                                BoundCommentRow(
                                    comment = comment,
                                    commentStateMap = commentStateMap,
                                    userId = userId,
                                    imageLoader = imageLoader,
                                    onCommentEvent = { viewModel.onEvent(it) }
                                )
                                HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(0.2f))
                            }

                            items(
                                count = replyItems.itemCount,
                                key = replyItems.itemKey { it.commentId }
                            ) { index ->
                                val item = replyItems[index]
                                if (item != null && item.commentId !in newlyAddedIds) {
                                    BoundCommentRow(
                                        comment = item,
                                        commentStateMap = commentStateMap,
                                        userId = userId,
                                        imageLoader = imageLoader,
                                        onCommentEvent = { viewModel.onEvent(it) }
                                    )
                                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(0.2f))
                                }
                            }



                            pagingAppendItems(
                                items = replyItems,
                                context = context
                            )

                        }


                    }
                    PullToRefreshDefaults.Indicator(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                }



            },
            bottomBar = {
                CommentInput(
                    comment = { viewModel.commentText.value },
                    onCommentChange = { viewModel.onEvent(CommentEvent.TypeComment(it)) },
                    onPostClick = {
                        if(viewModel.commentText.value.isNotEmpty())
                            viewModel.onEvent(CommentEvent.PostReply)
                    },
                    isAnonymous = anonymousChecked,
                    onAnonymousChange = { viewModel.onEvent(CommentEvent.ToggleAnonymous(it)) }
                )
            },
            lazyColumnExist = true
        )

    }


}
