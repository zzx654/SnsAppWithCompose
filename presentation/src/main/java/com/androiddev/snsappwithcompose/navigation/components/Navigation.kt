package com.androiddev.snsappwithcompose.navigation.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.androiddev.snsappwithcompose.components.CropScreen
import com.androiddev.snsappwithcompose.auth.signin.InitScreen
import com.androiddev.snsappwithcompose.auth.signin.SignInScreen
import com.androiddev.snsappwithcompose.auth.signup.AuthPhoneScreen
import com.androiddev.snsappwithcompose.auth.signup.EmailSignUpScreen
import com.androiddev.snsappwithcompose.createprofile.CreateProfileScreen
import com.androiddev.snsappwithcompose.home.HomeScreen
import com.androiddev.snsappwithcompose.upload_post.UploadPostScreen
import com.androiddev.snsappwithcompose.util.Screen

@RequiresApi(Build.VERSION_CODES.N)
@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.InitScreen) {
        composable<Screen.SignInScreen> {
            BackHandler(true) {
            }
            SignInScreen(navController)
        }
        composable<Screen.AuthPhoneScreen> {
            BackHandler(true) {
            }
            AuthPhoneScreen(navController = navController, navBackStackEntry = it)
        }
        composable<Screen.SignUpScreen> {
            BackHandler(true) {
            }
            EmailSignUpScreen(navController = navController, navBackStackEntry = it)
        }
        composable<Screen.CreateprofileScreen> {
            BackHandler(true) {
            }
            CreateProfileScreen(navController = navController, navBackStackEntry = it)
        }
        composable<Screen.HomeScreen> {
            BackHandler(true) {
            }
            HomeScreen(navController = navController)
        }
        composable<Screen.InitScreen> {
            BackHandler(true) {
            }
            InitScreen(navController = navController)
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

    }
}