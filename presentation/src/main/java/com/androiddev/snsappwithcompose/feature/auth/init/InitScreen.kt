package com.androiddev.snsappwithcompose.feature.auth.init

import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.viewmodel.UserViewModel
import com.androiddev.snsappwithcompose.common.component.AlertDialog
import com.androiddev.snsappwithcompose.common.component.LoadingProgressIndicator
import com.androiddev.snsappwithcompose.common.state.UiEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun InitScreen(
    navController: NavController,
    viewModel: SignInWithTokenViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).also {
                        it.setGravity(Gravity.BOTTOM, 0, 130)
                        it.show()
                    }
                }
                is UiEvent.navigate -> {
                    event.userId?.let {
                        userViewModel.setUserId(it)
                    }
                    navController.navigate(event.screen)
                }

                else -> null
            }
        }
    }
    AlertDialog(
        title = {viewModel.alertDialogState.value.title},
        cancelText = {viewModel.alertDialogState.value.cancelText},
        confirmText = {viewModel.alertDialogState.value.confirmText},
        onClickConfirm = viewModel.alertDialogState.value.onClickConfirm,
        onClickCancel = viewModel.alertDialogState.value.onClickCancel
    )

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

            LoadingProgressIndicator(modifier = Modifier.align(Alignment.BottomCenter).padding(150.dp),{ viewModel.isLoading.value})
        }


}