package com.androiddev.snsappwithcompose.feature.userprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import coil3.imageLoader
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.androiddev.domain.model.MediaPost
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.component.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.feature.home.user.UserEvent
import com.androiddev.snsappwithcompose.feature.home.user.UserViewModel
import com.androiddev.snsappwithcompose.feature.mediaviewer.MediaViewerArgs
import com.androiddev.snsappwithcompose.feature.userprofile.component.HomeTab
import com.androiddev.snsappwithcompose.feature.userprofile.component.MediaGridTab
import com.androiddev.snsappwithcompose.feature.userprofile.component.UserProfileHeader
import kotlinx.coroutines.launch

@Composable
fun UserProfileScreen(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
    userPostsViewModel: UserPostsViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    userProfileViewModel:UserProfileViewModel
) {

    var args = navBackStackEntry.toRoute<Screen.UserProfileScreen>()

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { userProfileViewModel.tabs.size }
    )
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val pagerNestedScrollConnection = rememberNestedScrollConnection(scrollState)
    val isFollowing = userViewModel.followUserStatusMap[args.userId]
    val context = LocalContext.current
    val imageLoader = remember {
        context.imageLoader.newBuilder()
            .crossfade(false)
            .logger(DebugLogger())
            .build()
    }
    val focusManager = LocalFocusManager.current


    val userInfoState = userViewModel.userInfo.value

    val openMediaViewer: (List<MediaPost>, Int, UserContent) -> Unit =
        { mediaList, index, type ->

            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set(MediaViewerArgs.MEDIA, mediaList)

            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set(MediaViewerArgs.CLICKED_INDEX, index)

            when (type) {
                UserContent.IMAGE ->
                    navController.navigate(Screen.ImageViewerScreen)

                UserContent.VIDEO -> {
                    navController.navigate(Screen.VideoViewerScreen)
                }


                else -> Unit
            }
        }
    LaunchedEffect(args.userId) {

        //userViewModel.onEvent(UserEvent.GetUserInfo(args.userId))
        userViewModel.refreshUser(args.userId)
        userPostsViewModel.initUserPosts(args.userId)
    }

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                CenterAlignedTopBar(
                    title = userInfoState?.nickname ?: "",
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier.padding(padding)
        ) {
            val screenHeight = maxHeight


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = scrollState)
            ) {
                Spacer(modifier = Modifier.height(25.dp))

                UserProfileHeader(
                    modifier = Modifier.padding(horizontal = 19.dp),
                    user = userInfoState,
                    imageLoader = imageLoader
                )

                Spacer(modifier = Modifier.height(20.dp))

                ActionSection(
                    modifier = Modifier.padding(horizontal = 19.dp),
                    isFollowing = isFollowing?:false,
                    toggleFollow = { userViewModel.onEvent(UserEvent.ToggleFollowUser(args.userId))}
                )

                Spacer(modifier = Modifier.height(40.dp))

                Column(modifier = Modifier.height(screenHeight)) {
                    // 탭바 영역
                    SecondaryTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        indicator = {
                            androidx.compose.material3.TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(pagerState.currentPage),
                                color = Color.Black
                            )
                        },
                        containerColor = Color.White
                    ) {

                        userProfileViewModel.tabs.forEachIndexed { index, tab ->

                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            ) {
                                Text(
                                    text = when (tab) {
                                        UserContent.HOME -> getString(context, R.string.home)
                                        UserContent.IMAGE -> getString(context,R.string.image)
                                        UserContent.VIDEO -> getString(context,R.string.video)
                                    },
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        }
                    }

                    val canRefresh = scrollState.value == 0

                    HorizontalPager(
                        state = pagerState,
                        beyondViewportPageCount = 1,
                        modifier = Modifier
                            .fillMaxHeight()
                            .nestedScroll(pagerNestedScrollConnection)
                    ) { page ->

                        when (userProfileViewModel.tabs[page]) {

                            UserContent.HOME -> {

                                HomeTab(
                                    viewModel = userProfileViewModel,
                                    refreshUserInfo = { userViewModel.refreshUser(args.userId)},
                                    canRefresh = canRefresh
                                )
                            }

                            UserContent.IMAGE -> {

                                MediaGridTab(
                                    type = UserContent.IMAGE,
                                    viewModel = userProfileViewModel,
                                    onMediaClick = { mediaList, index ->
                                        openMediaViewer(mediaList, index, UserContent.IMAGE)
                                    },
                                    refreshUserInfo = { userViewModel.refreshUser(args.userId)},
                                    canRefresh = canRefresh
                                )
                            }

                            UserContent.VIDEO -> {

                                MediaGridTab(
                                    type = UserContent.VIDEO,
                                    viewModel = userProfileViewModel,
                                    onMediaClick = { mediaList, index ->
                                        openMediaViewer(mediaList, index, UserContent.VIDEO)
                                    },
                                    refreshUserInfo = { userViewModel.refreshUser(args.userId)},
                                    canRefresh = canRefresh
                                )
                            }
                        }
                    }


                }


            }



        }

    }
}
@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isPrimary) Color.Black else Color.LightGray
    val contentColor = if (isPrimary) Color.White else Color.Black

    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text)
        }


    }

}
@Composable
fun ActionSection(
    modifier:Modifier = Modifier,
    isFollowing:Boolean,
    toggleFollow:() -> Unit
) {
    //var isFollowing by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        ActionButton(
            text = if (isFollowing) "Following" else "Follow",
            icon = if (isFollowing) Icons.Default.Check else Icons.Default.Add,
            isPrimary = !isFollowing,
            onClick = { toggleFollow() },
            modifier = Modifier.weight(1f)
        )

        ActionButton(
            text = "Message",
            icon = Icons.Default.Email,
            isPrimary = false,
            onClick = { /* 채팅 */ },
            modifier = Modifier.weight(1f)
        )
    }
}








