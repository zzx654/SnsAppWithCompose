package com.androiddev.snsappwithcompose.feature.PostDetail


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.hilt.navigation.compose.hiltViewModel
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
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.material.icons.outlined.ThumbUpAlt
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat.getString
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.size.Scale
import coil3.util.DebugLogger
import com.androiddev.snsappwithcompose.common.base.component.BaseScaffold
import com.androiddev.snsappwithcompose.BuildConfig
import com.androiddev.snsappwithcompose.feature.PostDetail.audio.AudioViewModel
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.CommentEvent
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.state.CommentLikeState
import com.androiddev.snsappwithcompose.feature.PostDetail.vote.VoteEvent
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.viewmodel.UserViewModel
import com.androiddev.snsappwithcompose.common.component.AlertDialog
import com.androiddev.snsappwithcompose.feature.PostDetail.audio.AudioPlayer
import com.androiddev.snsappwithcompose.common.component.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.common.component.Chips
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.component.CommentInput
import com.androiddev.snsappwithcompose.feature.PostDetail.comment.component.CommentItem
import com.androiddev.snsappwithcompose.common.component.CustomBottomSheetDialog
import com.androiddev.snsappwithcompose.common.component.CustomChip
import com.androiddev.snsappwithcompose.common.component.LoadingDialog
import com.androiddev.snsappwithcompose.feature.PostDetail.vote.component.PollCard
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.ui.theme.profileBorder
import com.androiddev.snsappwithcompose.common.model.MenuItem
import com.androiddev.snsappwithcompose.common.state.UiEvent
import kotlinx.coroutines.launch
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class, ExperimentalPagerApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ContextCastToActivity")
@Composable
fun PostDetailScreen(
    post: PostPreview?,
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
    userViewModel: UserViewModel,
    audioViewModel: AudioViewModel = hiltViewModel(),
    viewModel: PostDetailsViewModel = hiltViewModel(),
) {

    val currentPost = viewModel.post.value
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    val imageLoader = remember {
        context.imageLoader.newBuilder()
            .crossfade(false)
            .logger(DebugLogger())
            .build()
    }
    val dropdownMenuItem = if(post?.userId == userViewModel.userId.value) {
        listOf(
            MenuItem(getString(context,R.string.edit)){ navController.navigate(Screen.UploadPostScreen(viewModel.post.value))},
            MenuItem(getString(context,R.string.delete)) { viewModel.onPostDetailEvent(
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

    val pagerState = rememberPagerState(
        initialPage = 0
    )
    var pendingScrollByCount by remember { mutableStateOf(0) }
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

                    viewModel.onCommentEvent(CommentEvent.LoadNextComments)
                }
            }
    }
    val getCommentsState = viewModel.getCommentsState.value
    LaunchedEffect(post) {
        if(viewModel.post.value==null) {
            post?.let {

                viewModel.initPost(
                    isLiked = it.isliked,
                    post = it
                )
                audioViewModel.setAudioAvailable(!it.audio.isNullOrEmpty(),BuildConfig.BASE_URL+it.audio,it.nickname)
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
    val editedPost = navBackStackEntry.savedStateHandle.get<PostPreview>(getString(context,R.string.editedPost))
    editedPost?.let { post ->
        viewModel.initPost(
            isLiked = post.isliked,
            post = post
        )
        audioViewModel.setAudioAvailable(!post.audio.isNullOrEmpty(),BuildConfig.BASE_URL+post.audio,post.nickname)
        navBackStackEntry.savedStateHandle.set<PostPreview>(getString(context,R.string.editedPost),null)
    }
    AlertDialog(
        title = { viewModel.alertDialogState.value.title },
        cancelText = { viewModel.alertDialogState.value.cancelText },
        confirmText = { viewModel.alertDialogState.value.confirmText },
        onClickConfirm = viewModel.alertDialogState.value.onClickConfirm,
        onClickCancel = viewModel.alertDialogState.value.onClickCancel
    )
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
            if (post != null) {
                CenterAlignedTopBar(
                    title = currentPost?.nickname?:"",
                    onBackClick = { navController.popBackStack() },
                    actions = {
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
                                        item.onClick() //  각 항목의 고유한 onClick 실행
                                        dropdownMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
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
                        Column {

                            currentPost?.tags?.let { tags ->
                                Spacer(modifier = Modifier.height(10.dp))
                                Chips(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp),
                                    list = tags.map{"#$it"},
                                    chip = { data: String, index: Int ->
                                        CustomChip(
                                            backgroundColor = Color.Gray,
                                            text = data,
                                        )
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(if (currentPost?.tags == null) 15.dp else 5.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                ProfileImage(
                                    profileImage = currentPost?.profileImage?:"",
                                    gender = currentPost?.gender?:"",
                                    anonymous = post?.anonymous?:false,
                                    context = context,
                                    imageLoader = imageLoader
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        currentPost?.nickname?:"",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${currentPost?.elapsedTime} · ${currentPost?.distance?:0}km ",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                            Divider(
                                color = Color.LightGray,
                                thickness = 1.dp, // 또는 0.5.dp 등
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                            Text(
                                text = currentPost?.text?:"",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                            currentPost?.images?.let { images ->
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                        //.height(270.dp),
                                    count = currentPost?.imageSize ?: 0
                                ) { page ->
                                    // 여기에 페이지별로 보여줄 UI 구현 (예: 이미지)
                                    // 예시:
                                    // AsyncImage(model = images[page], contentDescription = null)
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(BuildConfig.BASE_URL + images[page])
                                            .build(),
                                        imageLoader = imageLoader,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                            //.heightIn(min = 200.dp, max = 400.dp)
                                            .padding(horizontal = 24.dp),

                                        contentScale = ContentScale.Fit,
                                        contentDescription = null
                                    )

                                }

                                Spacer(modifier = Modifier.height(15.dp))
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    if(pagerState.pageCount > 1){
                                        HorizontalPagerIndicator(
                                            //  modifier = Modifier.align(Alignment.TopCenter),
                                            pagerState = pagerState,
                                            //modifier = Modifier.padding(16.dp), // align 대신 padding 등으로 조절
                                            activeColor = Color.Black,
                                            inactiveColor = Color.LightGray
                                        )
                                    }

                                }


                            }

                            Spacer(modifier = Modifier.height(if (currentPost?.images == null) 0.dp else 15.dp))
                            PollCard(
                                voteState = viewModel.voteState.value,
                                onOptionSelected = { optionId -> viewModel.onVoteEvent(VoteEvent.SelectOption(optionId))},
                                onVoteClick = {
                                    viewModel.onVoteEvent(VoteEvent.OnVoteClick)
                                }
                            )
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AudioPlayer(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd) // 오른쪽 끝
                                        .padding(end = 8.dp,top = 10.dp),
                                    viewModel = audioViewModel,
                                    url = currentPost?.audio
                                )
                            }


                            Divider(
                                color = Color.LightGray,
                                thickness = 1.dp, // 또는 0.5.dp 등
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround

                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(vertical = 13.dp)) {
                                    androidx.compose.material3.Icon(
                                        imageVector = if (viewModel.isLiked.value) Icons.Filled.ThumbUpAlt else Icons.Outlined.ThumbUpAlt,
                                        contentDescription = null,
                                        tint = Color.DarkGray.copy(0.8f),
                                        modifier = Modifier
                                            .clickable {
                                                //viewModel.onEvent(
                                                //    PostDetailEvent.ToggleLikePost(post.postId)
                                                //)
                                                coroutineScope.launch {
                                                    listState.scrollToItem(100)  // reverseLayout=true 이므로 0번이 가장 아래임
                                                }
                                            }
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = getString(context,R.string.like), color = Color.DarkGray.copy(0.8f))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(vertical = 13.dp)) {
                                    androidx.compose.material3.Icon(
                                        imageVector = if (viewModel.isLiked.value) Icons.Filled.ThumbUpAlt else Icons.Outlined.ThumbUpAlt,
                                        contentDescription = null,
                                        tint = Color.DarkGray.copy(0.8f),
                                        modifier = Modifier
                                            .clickable {
                                                viewModel.onPostDetailEvent(
                                                    PostDetailEvent.ToggleLikePost(
                                                        currentPost?.postId ?: 0
                                                    )
                                                )
                                            }
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = getString(context,R.string.like),color = Color.DarkGray.copy(0.8f))
                                }

                            }
                            Divider(
                                color = Color.LightGray,
                                thickness = 1.dp, // 또는 0.5.dp 등
                                modifier = Modifier
                                    .fillMaxWidth()
                            )


                        }



                }
                item {
                    if(!viewModel.isCommentsEmpty.value) {
                        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp,vertical = 10.dp), contentAlignment = Alignment.TopStart) {
                            Row {
                                SelectableDotText(
                                    text = getString(context, CommentSortType.OLDEST.labelResId),
                                    selected = viewModel.commentSortType.value == CommentSortType.OLDEST,
                                    onClick = {//onEvent
                                        viewModel.onCommentEvent(CommentEvent.SetCommentSortType(
                                            CommentSortType.OLDEST
                                        ))
                                    },
                                )
                                Spacer(modifier = Modifier.width(9.dp))
                                SelectableDotText(
                                    text = getString(context, CommentSortType.POPULAR.labelResId),
                                    selected = viewModel.commentSortType.value == CommentSortType.POPULAR,
                                    onClick = {
                                        viewModel.onCommentEvent(CommentEvent.SetCommentSortType(
                                            CommentSortType.POPULAR
                                        ))
                                    },
                                )
                            }
                        }


                    }
                }
                item {
                    if(getCommentsState.isRefreshing) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                items(
                    getCommentsState.comments
                ) { comment ->

                    val commentLikeStatus = viewModel.commentLikeStatusMap[comment.commentId]?: CommentLikeState(isLiked = false,likeCount = 0)
                    CommentItem(
                        comment = comment,
                        isLiked = commentLikeStatus.isLiked,
                        likeCount = commentLikeStatus.likeCount,
                        imageLoader = imageLoader,
                        onLikeClick = {
                            comment.commentId?.let {
                                viewModel.onCommentEvent(CommentEvent.ToggleLikeComment(it))
                            }
                        },
                        onOptionClick = {
                            viewModel.onCommentEvent(
                                CommentEvent.ShowCommentOptions(
                                myUserId = userViewModel.userId.value,
                                commentUserId = comment.userId
                            ))
                        },
                        onCommentClick = {
                            viewModel.onCommentEvent(
                                CommentEvent.GotoReplyScreen(
                                commentId = comment.commentId?:0
                            ))
                        }
                    )
                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp
                    )
                }
                item {
                    if(getCommentsState.isLoading && getCommentsState.comments.isNotEmpty()) {
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
                //로딩중이 아닐때, comment가 없는상태에서 불러온결과 없을때
                item {
                    if(!getCommentsState.isLoading&&viewModel.isCommentsEmpty.value) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getString(context,R.string.comment_empty),
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

            }

        },
        bottomBar = {
            CommentInput(
                comment = viewModel.commentText.value,
                onCommentChange = { viewModel.onCommentEvent(CommentEvent.TypeComment(it)) },
                onPostClick = {
                    if(viewModel.commentText.value.isNotEmpty())
                        viewModel.onCommentEvent(CommentEvent.PostComment)
                },
                isAnonymous = viewModel.anonymousChecked.value,
                onAnonymousChange = { viewModel.onCommentEvent(CommentEvent.ToggleAnonymous(it)) }
            )
        },
        lazyColumnExist = true
    )


}

@Composable
fun ProfileImage(profileImage: String?, gender: String, anonymous: Boolean,context: Context,imageLoader: ImageLoader) {
    val sizeDp = 42.dp
    val sizePx = with(LocalDensity.current) { sizeDp.roundToPx() }
    if (profileImage == null || anonymous) {
        Image(
            contentScale = ContentScale.Crop,
            painter = painterResource(id = if (gender == getString(context,R.string.male)) R.drawable.person_male else if (gender == getString(context,R.string.female)) R.drawable.person_female else R.drawable.person_none),
            contentDescription = null,
            modifier = Modifier
                .size(42.dp)
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
            modifier = Modifier
                .size(sizeDp)
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