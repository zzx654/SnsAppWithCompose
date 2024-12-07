package com.androiddev.snsappwithcompose.navigation.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.androiddev.snsappwithcompose.auth.signin.SignInScreen
import com.androiddev.snsappwithcompose.auth.signup.AuthPhoneScreen
import com.androiddev.snsappwithcompose.auth.signup.EmailSignUpScreen
import com.androiddev.snsappwithcompose.util.Screen

@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.LoginScreen) {
        composable<Screen.LoginScreen> {
            SignInScreen(navController)
        }
        composable<Screen.AuthPhoneScreen> {
            AuthPhoneScreen(navController = navController, navBackStackEntry = it)
        }
        composable<Screen.SignUpScreen> {
            EmailSignUpScreen(navController = navController, navBackStackEntry = it)
        }
    }
}