package com.androiddev.snsappwithcompose.Reply

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil3.imageLoader
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.androiddev.domain.model.Comment
import com.androiddev.snsappwithcompose.BaseScaffold
import com.androiddev.snsappwithcompose.PostDetail.CommentEvent
import com.androiddev.snsappwithcompose.PostDetail.PostDetailEvent
import com.androiddev.snsappwithcompose.UserViewModel
import com.androiddev.snsappwithcompose.components.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.components.CommentInput
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

                    //viewModel.onEvent(PostDetailEvent.LoadNextComments)
                }
            }
    }
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

        },
        bottomBar = {
            CommentInput(
                comment = viewModel.commentText.value,
                onCommentChange = { viewModel.onEvent(CommentEvent.TypeComment(it)) },
                onPostClick = {
                    if(viewModel.commentText.value.isNotEmpty())
                        viewModel.onEvent(CommentEvent.PostComment)
                },
                isAnonymous = viewModel.anonymousChecked.value,
                onAnonymousChange = { viewModel.onEvent(CommentEvent.ToggleAnonymous(it)) }
            )
        },
        lazyColumnExist = true
    )

}
