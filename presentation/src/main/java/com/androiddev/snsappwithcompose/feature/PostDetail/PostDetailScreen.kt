package com.androiddev.snsappwithcompose.feature.PostDetail


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.androiddev.domain.model.PostPreview
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat.getString
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.size.Scale
import com.androiddev.snsappwithcompose.common.base.component.BaseScaffold
import com.androiddev.snsappwithcompose.BuildConfig
import com.androiddev.snsappwithcompose.feature.PostDetail.audio.AudioViewModel
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.CommentEvent
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.viewmodel.CurrentUserViewModel
import com.androiddev.snsappwithcompose.common.component.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.component.CommentInput
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.ui.theme.profileBorder
import com.androiddev.snsappwithcompose.common.model.MenuItem
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.androiddev.domain.model.Post
import com.androiddev.snsappwithcompose.common.base.BaseScreen
import com.androiddev.snsappwithcompose.common.component.AlertDialogg
import com.androiddev.snsappwithcompose.common.component.SelectorBottomSheetDialog
import com.androiddev.snsappwithcompose.common.util.generateDisplayName
import com.androiddev.snsappwithcompose.feature.PostDetail.component.PostDetailContent

private const val NOTIFICATION_COMMENT_INDEX = 2
private const val NOTIFICATION_REPLY_INDEX = 3
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ContextCastToActivity")
@Composable
fun PostDetailScreen(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
    currentUserViewModel: CurrentUserViewModel,
    audioViewModel: AudioViewModel = hiltViewModel(),
    postViewModel: PostDetailsViewModel
) {
    val postDetailUiState by postViewModel.postDetailUiState.collectAsStateWithLifecycle()
    val commentItems =  postViewModel.pagingCommentStream.collectAsLazyPagingItems()
    val newlyAddedComments by postViewModel.newlyAddedComments.collectAsStateWithLifecycle()
    val userId by currentUserViewModel.userId.collectAsStateWithLifecycle()
    val commentSortType by postViewModel.commentSortType.collectAsStateWithLifecycle()
    val commentStateMap by postViewModel.commentStateMap.collectAsStateWithLifecycle()
    val anonymousChecked by postViewModel.anonymousChecked.collectAsStateWithLifecycle()

    val notificationComment by postViewModel.notificationComment.collectAsStateWithLifecycle()
    val notificationReply by postViewModel.notificationReply.collectAsStateWithLifecycle()
    val alertDialogState by postViewModel.alertDialogState.collectAsStateWithLifecycle()
    val bottomSheetDialogState by postViewModel.bottomSheetDialogState.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()
    val dropdownMenuItem = if(postDetailUiState.post?.userId == userId) {
        listOf(
            MenuItem(getString(context,R.string.edit)){ navController.navigate(Screen.UploadPostScreen(postDetailUiState.post?.postId))},
            MenuItem(getString(context,R.string.delete)) { postViewModel.onPostDetailEvent(
                PostDetailEvent.DeletePost
            )}
        )

    } else {
        listOf(
            MenuItem(getString(context,R.string.block_user)){},
            MenuItem(getString(context,R.string.report)) {},
            MenuItem(getString(context,R.string.request_chat)) {}
        )
    }
    var pendingScrollByCount by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var imeHeigh = remember { mutableStateOf(0) }
    val ime = WindowInsets.ime
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

    LaunchedEffect(postDetailUiState.audioUrl) {
        val post = postDetailUiState.post
        postDetailUiState.audioUrl?.let {
            if (post != null) {
                audioViewModel.prepareAudio(
                    url = BuildConfig.BASE_URL + it,
                    nickname = post.anonymousNickname ?: post.nickname ?:""
                )
            }
        }
    }
    LaunchedEffect(
        notificationComment,
        notificationReply
    ) {
        // Compose 레이아웃 계산을 위해 약간 delay

        val indexToScroll = when {
            notificationReply != null -> NOTIFICATION_REPLY_INDEX
            notificationComment != null -> NOTIFICATION_COMMENT_INDEX
            else -> null
        }

        indexToScroll?.let {
            listState.animateScrollToItem(
                index = it,
                scrollOffset = -100 // optional, 위쪽 여백
            )
        }
    }

    val editedPost = navBackStackEntry.savedStateHandle.get<Post>(getString(context,R.string.editedPost))
    editedPost?.let { post ->
        postViewModel.onPostDetailEvent(PostDetailEvent.LoadEditedPostDetails(post))
        //post.audio?.let {
         //   audioViewModel.prepareAudio(
          //      url = BuildConfig.BASE_URL+it,
           //     nickname = post.nickname
            //)
       // }
        navBackStackEntry.savedStateHandle.set<PostPreview>(getString(context,R.string.editedPost),null)
    }
    AlertDialogg (
        title = alertDialogState.title?.asString() ?:"",
        cancelText = alertDialogState.cancelText?.asString() ?:"",
        confirmText = alertDialogState.confirmText?.asString() ?:"",
        onClickConfirm = alertDialogState.onClickConfirm,
        onClickCancel = alertDialogState.onClickCancel
    )

    SelectorBottomSheetDialog(bottomSheetDialogState)
    BaseScreen(
        viewModel = postViewModel,
        navController = navController,
    ) {
        BaseScaffold(
            modifier = Modifier.fillMaxWidth(),
            focusManager = focusManager,
            scrollState = scrollState,
            topBar = {
                val post = postDetailUiState.post
                val titleText = if (post?.nickname != null) {
                    generateDisplayName(
                        context,
                        post.nickname,
                        post.anonymousNickname
                    )
                } else {
                    ""
                }
                CenterAlignedTopBar(
                    title = titleText,
                    onBackClick = { navController.popBackStack() },
                    rightAction = {
                        // post가 있을 때만 우측 옵션 메뉴 표시
                        if (postDetailUiState.post != null) {
                            IconButton(onClick = { dropdownMenuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More options"
                                )
                            }
                            DropdownMenu(
                                expanded = dropdownMenuExpanded,
                                onDismissRequest = { dropdownMenuExpanded = false }
                            ) {
                                dropdownMenuItem.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item.label) },
                                        onClick = {
                                            item.onClick()
                                            dropdownMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            },
            content = {
                PostDetailContent(
                    navController = navController,
                    userId = userId,
                    postDetailUiState = postDetailUiState,
                    commentSortType = commentSortType,
                    onVoteEvent = {
                        postViewModel.onVoteEvent(it)
                    },
                    onPostDetailEvent = {
                        postViewModel.onPostDetailEvent(it)
                    },
                    notificationComment = notificationComment,
                    notificationReply = notificationReply,
                    audioViewModel = audioViewModel,
                    newlyAddedComments = newlyAddedComments,
                    pagingComments = commentItems,
                    onPagingRefreshComplete = { postViewModel.setLoading(false) },
                    onCommentEvent = {
                        postViewModel.onCommentEvent(it)
                    },
                    onRefresh = { postViewModel.fetchPostDetail() },
                    commentStateMap = commentStateMap
                )

            },
            bottomBar = {
                CommentInput(
                    comment = { postViewModel.commentText.value },
                    onCommentChange = { postViewModel.onCommentEvent(CommentEvent.TypeComment(it)) },
                    onPostClick = {
                        if(postViewModel.commentText.value.isNotEmpty())
                            postViewModel.onCommentEvent(CommentEvent.PostComment)
                    },
                    isAnonymous = anonymousChecked,
                    onAnonymousChange = { postViewModel.onCommentEvent(CommentEvent.ToggleAnonymous(it)) }
                )
            },
            lazyColumnExist = true
        )
    }
}



@Composable
fun ProfileImage(
    modifier:Modifier = Modifier.size(42.dp),
    profileImage: String?, gender: String,
    anonymous: Boolean = false,
    context: Context,
    imageLoader: ImageLoader
) {
    val sizeDp = 42.dp
    val sizePx = with(LocalDensity.current) { sizeDp.roundToPx() }
    if (profileImage == null || anonymous) {
        Image(
            contentScale = ContentScale.Crop,
            painter = painterResource(id = if (gender == getString(context,R.string.male)) R.drawable.person_male else if (gender == getString(context,R.string.female)) R.drawable.person_female else R.drawable.person_none),
            contentDescription = null,
            modifier = modifier
                .clip(CircleShape) // clip to the circle shape
                .border(1.dp, profileBorder, CircleShape)
        )
    } else {
        val imageRequest = remember(profileImage) {
            ImageRequest.Builder(context)
                .data(BuildConfig.BASE_URL + profileImage)
                .size(sizePx)
                .scale(Scale.FILL)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .placeholder(R.drawable.person_none)
                .error(R.drawable.person_none)
                .build()
        }
        AsyncImage(
            model = imageRequest,
            imageLoader = imageLoader,
            modifier = modifier
                .clip(CircleShape) // clip to the circle shape
                .border(1.dp, profileBorder, CircleShape),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }
}
@SuppressLint("RememberReturnType")
@Composable
fun SelectableDotText(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dotColor = if (selected) Color.Black else Color.Gray.copy(alpha = 0.7f)
    val textColor = if (selected) Color.Black else Color.Gray.copy(alpha = 0.7f)
    val fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
        //.padding(8.dp)
    ) {
        Text(
            text = "•",
            color = dotColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}
@Composable
fun ChatMessages(
    messages: List<String>,
    modifier: Modifier = Modifier,
    isLoad: Boolean,
    listState: LazyListState
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        reverseLayout = true // 최신 메시지가 아래에 오도록 역순 배치
    ) {

        item {
            if (isLoad) {
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

        items(messages.asReversed()) { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFDCF8C6), shape = MaterialTheme.shapes.medium)
                    .padding(12.dp)
            ) {
                Text(text = msg, fontSize = 16.sp)
            }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.Red)
            )
        }
    }
}