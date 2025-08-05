package com.androiddev.snsappwithcompose.navigation.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.androiddev.domain.model.PostPreview
import com.androiddev.snsappwithcompose.MainScaffold
import com.androiddev.snsappwithcompose.PostDetail.PostDetailScreen
import com.androiddev.snsappwithcompose.UserViewModel
import com.androiddev.snsappwithcompose.components.CropScreen
import com.androiddev.snsappwithcompose.auth.signin.InitScreen
import com.androiddev.snsappwithcompose.auth.signin.SignInScreen
import com.androiddev.snsappwithcompose.auth.signup.AuthPhoneScreen
import com.androiddev.snsappwithcompose.auth.signup.EmailSignUpScreen
import com.androiddev.snsappwithcompose.createprofile.CreateProfileScreen
import com.androiddev.snsappwithcompose.upload_post.UploadPostScreen

@RequiresApi(Build.VERSION_CODES.N)
@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun Navigation(navController: NavHostController,modifier:Modifier) {

    val userViewModel: UserViewModel = hiltViewModel()
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
            BackHandler(true) {
            }
            MainScaffold(rootNavController = navController,startTab = Screen.HomeScreen)
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
        composable<Screen.UploadPostScreen> {
            BackHandler(true) {
            }
            UploadPostScreen(navController = navController)
        }
        composable<Screen.PostDetailScreen>(
            typeMap = postTypeMap
        ) {
            //여기
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


    }
}