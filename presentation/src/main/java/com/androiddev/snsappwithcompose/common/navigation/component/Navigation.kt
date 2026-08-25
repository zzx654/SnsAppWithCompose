package com.androiddev.snsappwithcompose.common.navigation.component

import android.annotation.SuppressLint
import android.os.Build
import android.view.Gravity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.androiddev.snsappwithcompose.common.component.MainScaffold
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.util.PendingNotificationHandler

import com.androiddev.snsappwithcompose.feature.PostDetail.PostDetailScreen
import com.androiddev.snsappwithcompose.feature.Reply.ReplyScreen
import com.androiddev.snsappwithcompose.common.viewmodel.CurrentUserViewModel
import com.androiddev.snsappwithcompose.feature.PostDetail.PostDetailsViewModel
import com.androiddev.snsappwithcompose.feature.auth.init.InitScreen
import com.androiddev.snsappwithcompose.feature.auth.signin.SignInScreen
import com.androiddev.snsappwithcompose.feature.auth.signup.authphone.AuthPhoneScreen
import com.androiddev.snsappwithcompose.feature.auth.signup.emailsignup.EmailSignUpScreen
import com.androiddev.snsappwithcompose.feature.createprofile.CreateProfileScreen
import com.androiddev.snsappwithcompose.feature.createprofile.component.CropScreen
import com.androiddev.snsappwithcompose.feature.home.tagposts.TagPostScreen
import com.androiddev.snsappwithcompose.feature.home.tags.TagViewModel
import com.androiddev.snsappwithcompose.feature.home.user.UserViewModel
import com.androiddev.snsappwithcompose.feature.notification.NotificationViewModel
import com.androiddev.snsappwithcompose.feature.upload_post.UploadPostScreen
import com.androiddev.snsappwithcompose.feature.userprofile.UserProfileScreen
import com.androiddev.snsappwithcompose.feature.userprofile.UserProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.navigation.compose.navigation
import com.androiddev.snsappwithcompose.feature.PostDetail.VisualMediaScreen
import com.androiddev.snsappwithcompose.feature.mediaviewer.imageviewer.ImageViewerScreen
import com.androiddev.snsappwithcompose.feature.mediaviewer.videoviewer.VideoViewerScreen
import com.androiddev.snsappwithcompose.feature.upload_post.UploadPostViewModel
import com.androiddev.snsappwithcompose.feature.upload_post.VideoPlayerScreen
import com.androiddev.snsappwithcompose.feature.upload_post.VisualMediaEditScreen


@RequiresApi(Build.VERSION_CODES.N)
@SuppressLint("UnrememberedGetBackStackEntry", "NewApi")
@Composable
fun Navigation(notificationViewModel: NotificationViewModel,navController: NavHostController,modifier:Modifier) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentUserViewModel: CurrentUserViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
    val pending by notificationViewModel.pending.collectAsState()
    val isSignedIn by currentUserViewModel.isSignedIn.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        notificationViewModel.eventFlow.collectLatest { event ->
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
    NavHost(
        modifier = modifier,navController = navController,
        startDestination = Screen.InitScreen
    ) {

        composable<Screen.InitScreen> {
            //여기
            BackHandler(true) {
            }
            InitScreen(navController = navController,currentUserViewModel = currentUserViewModel, notificationViewModel = notificationViewModel)
        }
        composable<Screen.MainScreen> {
            val tagViewModel: TagViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()// 그냥 생성

            val userViewModel: UserViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
            BackHandler(true) {
            }
            MainScaffold(
                rootNavController = navController,
                startTab = Screen.HomeScreen,
                tagViewModel = tagViewModel,
                userViewModel = userViewModel,
                notificationViewModel = notificationViewModel
            )

        }
        composable<Screen.SignInScreen> {
            //여기
            BackHandler(true) {
            }
            SignInScreen(navController = navController,currentUserViewModel = currentUserViewModel,notificationViewModel = notificationViewModel)
        }
        composable<Screen.AuthPhoneScreen> {
            BackHandler(true) {
            }
            AuthPhoneScreen(
                navController = navController,
                navBackStackEntry = it
            )
        }
        composable<Screen.SignUpScreen> {
            BackHandler(true) {
            }
            EmailSignUpScreen(
                navController = navController,
                navBackStackEntry = it
            )
        }
        composable<Screen.CreateprofileScreen> {
            BackHandler(true) {
            }
            CreateProfileScreen(
                navController = navController,
                navBackStackEntry = it
            )
        }

        composable<Screen.CropScreen> {
            BackHandler(true) {
            }
            CropScreen(navController = navController,navBackStackEntry = it)
        }
        composable<Screen.VideoPlayerScreen> {
            BackHandler(true) {
            }
            VideoPlayerScreen(navController = navController,navBackStackEntry = it)

        }
        navigation(
            route = UPLOAD_FLOW,
            startDestination = Screen.UploadPostScreen::class.qualifiedName!!
        ) {

            composable<Screen.UploadPostScreen> {
                val parentEntry = remember {
                    navController.getBackStackEntry(UPLOAD_FLOW)
                }

                val uploadViewModel: UploadPostViewModel =
                    androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(parentEntry)

                BackHandler(true) {}

                UploadPostScreen(
                    navController = navController,
                    viewModel = uploadViewModel
                )

                PendingNotificationHandler(notificationViewModel)
            }

            composable<Screen.MediaEditScreen> {
                val parentEntry = remember {
                    navController.getBackStackEntry(UPLOAD_FLOW)
                }

                val uploadViewModel: UploadPostViewModel =
                    androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(parentEntry)

                BackHandler(true) {}

                VisualMediaEditScreen(
                    navController = navController,
                    viewModel = uploadViewModel
                )
            }
            composable<Screen.MediaScreen>(
                typeMap = mediaTypeMap
            ) {
                BackHandler(true) {}

                VisualMediaScreen(
                    navController = navController,
                    navBackStackEntry = it
                )


            }
        }
        /**composable<Screen.UploadPostScreen>(
            typeMap = postTypeMap
        ) {
            val post = it.toRoute<Screen.UploadPostScreen>().post
            BackHandler(true) {
            }
            UploadPostScreen(
                post = post,
                navController = navController)
            PendingNotificationHandler(notificationViewModel)
        }
        composable<Screen.MediaPreviewScreen> {

        }**/
        composable<Screen.PostDetailScreen>{
            val postViewModel: PostDetailsViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()

           BackHandler(true) {
            }
            PostDetailScreen(
                postViewModel = postViewModel,
                currentUserViewModel = currentUserViewModel,
                navController = navController,
                navBackStackEntry = it,
            )
            PendingNotificationHandler(notificationViewModel)
        }
        composable<Screen.UserProfileScreen> {
            val parentEntry = remember { navController.getBackStackEntry(Screen.MainScreen) }
            val userViewModel: UserViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(parentEntry)
            val userProfileViewModel: UserProfileViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
            BackHandler(true) {
            }
            UserProfileScreen(
                userViewModel = userViewModel,
                userProfileViewModel = userProfileViewModel,
                navController = navController,
                navBackStackEntry = it,
            )
            PendingNotificationHandler(notificationViewModel)

        }
        composable<Screen.ImageViewerScreen> {

            BackHandler(true) {
            }
            ImageViewerScreen(navController = navController)

        }
        composable<Screen.VideoViewerScreen> {
            BackHandler(true) {

            }
            VideoViewerScreen(navController = navController)
        }
        composable<Screen.ReplyScreen>(
            typeMap = commentTypeMap
        ) {
            val comment = it.toRoute<Screen.ReplyScreen>().comment
            BackHandler(true) {
            }
            ReplyScreen(
                comment = comment,
                navController = navController,
                navBackStackEntry = it,
                currentUserViewModel = currentUserViewModel
            )
            PendingNotificationHandler(notificationViewModel)
        }
        composable<Screen.TagPostsScreen> {
            val parentEntry = remember { navController.getBackStackEntry(Screen.MainScreen) }
            val tagViewModel: TagViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(parentEntry)
            BackHandler(true) {
            }
            TagPostScreen(
                navController = navController,
                navBackStackEntry = it,
                viewModel = tagViewModel
            )
            PendingNotificationHandler(notificationViewModel)
        }



    }
}