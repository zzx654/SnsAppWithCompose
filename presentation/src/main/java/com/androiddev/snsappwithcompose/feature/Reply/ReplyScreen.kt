package com.androiddev.snsappwithcompose.feature.Reply

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil3.imageLoader
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.androiddev.domain.model.Comment
import com.androiddev.snsappwithcompose.common.base.component.BaseScaffold
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.CommentEvent
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.state.CommentLikeState
import com.androiddev.snsappwithcompose.common.viewmodel.UserViewModel
import com.androiddev.snsappwithcompose.common.component.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.component.CommentInput
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.component.CommentItem
import com.androiddev.snsappwithcompose.common.component.CustomBottomSheetDialog
import com.androiddev.snsappwithcompose.common.component.LoadingDialog
import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun ReplyScreen(
    comment: Comment?,
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
    viewModel: ReplyViewModel = hiltViewModel(),
    userViewModel: UserViewModel
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
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var imeHeigh = remember { mutableStateOf(0) }
    val ime = androidx.compose.foundation.layout.WindowInsets.ime
    val localDensity = LocalDensity.current
    val getCommentsState = viewModel.getCommentsState.value
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


    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                val totalItemsCount = listState.layoutInfo.totalItemsCount
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= totalItemsCount - 1&&imeHeigh.value == 0&&totalItemsCount>=10) {

                    viewModel.onEvent(CommentEvent.LoadNextComments)
                }
            }
    }
    LaunchedEffect(comment) {
        comment?.let {
            viewModel.initComment(comment)
        }
    }
    LoadingDialog {
        viewModel.isLoading.value
    }
    CustomBottomSheetDialog(
        { viewModel.customBottomSheetDialogState.value.showDialog },
        { viewModel.customBottomSheetDialogState.value.items },
        viewModel.customBottomSheetDialogState.value.onClickCancel
    )
    BaseScaffold(
        modifier = Modifier.fillMaxWidth(),
        focusManager = focusManager,
        scrollState = scrollState,
        topBar = {
            comment?.let {
                CenterAlignedTopBar(
                    title = it.nickname,
                    onBackClick = { navController.popBackStack() },
                )
            }
        },
        content = {
            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize(),
            ) {
                item {
                    comment?.let {
                        val commentLikeStatus = viewModel.commentLikeStatusMap[it.commentId]?: CommentLikeState(isLiked = false,likeCount = 0)
                        CommentItem(
                            comment = comment,
                            isLiked = commentLikeStatus.isLiked,
                            likeCount = commentLikeStatus.likeCount,
                            imageLoader = imageLoader,
                            onLikeClick = {
                                it.commentId?.let { commentId ->
                                    viewModel.onEvent(CommentEvent.ToggleLikeComment(commentId))
                                }
                            },
                            onOptionClick = {
                                //viewModel.onCommentEvent(CommentEvent.ShowCommentOptions(
                                 //   myUserId = userViewModel.userId.value,
                                 //   commentUserId = comment.userId
                                //))
                            },
                            onCommentClick = {
                                //viewModel.onCommentEvent(CommentEvent.GotoReplyScreen(
                                  //  commentId = comment.commentId?:0
                               // ))
                            }
                        )
                        Divider(
                            color = Color.LightGray,
                            thickness = 1.dp
                        )
                    }
                }
                items(   getCommentsState.comments
                ) { comment ->
                    val commentLikeStatus = viewModel.commentLikeStatusMap[comment.commentId]?: CommentLikeState(isLiked = false,likeCount = 0)

                    ReplyItem(
                        comment = comment,
                        isLiked = commentLikeStatus.isLiked,
                        likeCount = commentLikeStatus.likeCount,
                        imageLoader = imageLoader,
                        onLikeClick = {
                            comment.commentId?.let {
                                viewModel.onEvent(CommentEvent.ToggleLikeComment(it))
                            }
                        },
                        onOptionClick = {
                            viewModel.onEvent(
                                CommentEvent.ShowCommentOptions(
                                myUserId = userViewModel.userId.value,
                                commentUserId = comment.userId
                            ))
                        }
                    )
                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp
                    )
                }
                item {
                    if(getCommentsState.isLoading
                       // && getCommentsState.comments.isNotEmpty()
                        ) {
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

        },
        bottomBar = {
            CommentInput(
                comment = viewModel.commentText.value,
                onCommentChange = { viewModel.onEvent(CommentEvent.TypeComment(it)) },
                onPostClick = {
                    if(viewModel.commentText.value.isNotEmpty())
                        viewModel.onEvent(CommentEvent.PostReply)
                },
                isAnonymous = viewModel.anonymousChecked.value,
                onAnonymousChange = { viewModel.onEvent(CommentEvent.ToggleAnonymous(it)) }
            )
        },
        lazyColumnExist = true
    )

}
