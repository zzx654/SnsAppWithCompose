package com.androiddev.snsappwithcompose.navigation.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.androiddev.snsappwithcompose.auth.components.CropScreen
import com.androiddev.snsappwithcompose.auth.signin.InitScreen
import com.androiddev.snsappwithcompose.auth.signin.SignInScreen
import com.androiddev.snsappwithcompose.auth.signup.AuthPhoneScreen
import com.androiddev.snsappwithcompose.auth.signup.EmailSignUpScreen
import com.androiddev.snsappwithcompose.home.HomeScreen
import com.androiddev.snsappwithcompose.createprofile.CreateProfileScreen
import com.androiddev.snsappwithcompose.util.Screen

@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.CreateprofileScreen) {
        composable<Screen.SignInScreen> {
            SignInScreen(navController)
        }
        composable<Screen.AuthPhoneScreen> {
            AuthPhoneScreen(navController = navController, navBackStackEntry = it)
        }
        composable<Screen.SignUpScreen> {
            EmailSignUpScreen(navController = navController, navBackStackEntry = it)
        }
        composable<Screen.CreateprofileScreen> {
            CreateProfileScreen(navController = navController)
        }
        composable<Screen.HomeScreen> {
            HomeScreen(navController = navController)
        }
        composable<Screen.InitScreen> {
            InitScreen(navController = navController)
        }
        composable<Screen.CropScreen> {
            CropScreen(navController = navController,navBackStackEntry = it)
        }
    }
}