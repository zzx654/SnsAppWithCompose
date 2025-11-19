package com.androiddev.snsappwithcompose.common.navigation.component

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.androiddev.snsappwithcompose.common.component.MainScaffold
import com.androiddev.snsappwithcompose.feature.PostDetail.PostDetailScreen
import com.androiddev.snsappwithcompose.feature.Reply.ReplyScreen
import com.androiddev.snsappwithcompose.common.viewmodel.UserViewModel
import com.androiddev.snsappwithcompose.feature.auth.init.InitScreen
import com.androiddev.snsappwithcompose.feature.auth.signin.SignInScreen
import com.androiddev.snsappwithcompose.feature.auth.signup.authphone.AuthPhoneScreen
import com.androiddev.snsappwithcompose.feature.auth.signup.emailsignup.EmailSignUpScreen
import com.androiddev.snsappwithcompose.feature.createprofile.CreateProfileScreen
import com.androiddev.snsappwithcompose.feature.createprofile.component.CropScreen
import com.androiddev.snsappwithcompose.feature.home.tagposts.TagPostScreen
import com.androiddev.snsappwithcompose.feature.home.tags.TagViewModel
import com.androiddev.snsappwithcompose.feature.upload_post.UploadPostScreen

@RequiresApi(Build.VERSION_CODES.N)
@SuppressLint("UnrememberedGetBackStackEntry", "NewApi")
@Composable
fun Navigation(navController: NavHostController,modifier:Modifier) {

    val userViewModel: UserViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
    NavHost(
        modifier = modifier,navController = navController,
        startDestination = Screen.InitScreen
    ) {
        composable<Screen.InitScreen> {
            //여기
            BackHandler(true) {
            }
            InitScreen(navController = navController,userViewModel = userViewModel)
        }
        composable<Screen.MainScreen> {
            val tagViewModel: TagViewModel = hiltViewModel() // 그냥 생성

            BackHandler(true) {
            }
            MainScaffold(rootNavController = navController,startTab = Screen.HomeScreen,tagViewModel = tagViewModel)
        }
        composable<Screen.SignInScreen> {
            //여기
            BackHandler(true) {
            }
            SignInScreen(navController = navController,userViewModel = userViewModel)
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
        composable<Screen.UploadPostScreen>(
            typeMap = postTypeMap
        ) {
            val post = it.toRoute<Screen.PostDetailScreen>().post
            BackHandler(true) {
            }
            UploadPostScreen(
                post = post,
                navController = navController)
        }
        composable<Screen.PostDetailScreen>(
            typeMap = postTypeMap
        ) {

            val post = it.toRoute<Screen.PostDetailScreen>().post
           BackHandler(true) {
            }
            PostDetailScreen(
                post = post,
                userViewModel = userViewModel,
                navController = navController,
                navBackStackEntry = it,
            )
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
                userViewModel = userViewModel
            )
        }
        composable<Screen.TagPostsScreen> {
            val parentEntry = remember { navController.getBackStackEntry(Screen.MainScreen) }
            val tagViewModel: TagViewModel = hiltViewModel(parentEntry)
            BackHandler(true) {
            }
            TagPostScreen(
                navController = navController,
                navBackStackEntry = it,
                viewModel = tagViewModel
            )
        }


    }
}