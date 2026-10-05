package com.androiddev.snsappwithcompose.feature.auth.init

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.BaseScreen
import com.androiddev.snsappwithcompose.common.viewmodel.CurrentUserViewModel
import com.androiddev.snsappwithcompose.common.component.LoadingProgressIndicator
import com.androiddev.snsappwithcompose.common.component.AlertDialogg
import com.androiddev.snsappwithcompose.feature.notification.NotificationViewModel

@Composable
fun InitScreen(
    navController: NavController,
    viewModel: SignInWithTokenViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    currentUserViewModel: CurrentUserViewModel,
    notificationViewModel: NotificationViewModel
) {
    val alertDialogState by viewModel.alertDialogState.collectAsStateWithLifecycle()

    AlertDialogg(
        title =  alertDialogState.title?.asString()?:"" ,
        cancelText = alertDialogState.cancelText?.asString() ?:"",
        confirmText = alertDialogState.confirmText?.asString() ?:"",
        onClickConfirm = alertDialogState.onClickConfirm,
        onClickCancel = alertDialogState.onClickCancel
    )
    BaseScreen(
        viewModel = viewModel,
        currentUserViewModel = currentUserViewModel,
        navController = navController,
        loadingContent = {
            Box(modifier = Modifier.fillMaxSize()) {
                LoadingProgressIndicator(modifier = Modifier.align(Alignment.BottomCenter).padding(150.dp),{ viewModel.isLoading.value})
            }

        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),

            ){
            Column (modifier = Modifier.align(Alignment.Center)){
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(id = R.drawable.dog),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(30.dp))
            }


        }

    }




}