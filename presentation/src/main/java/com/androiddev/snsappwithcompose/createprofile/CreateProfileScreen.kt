package com.androiddev.snsappwithcompose.createprofile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.components.CustomBottomSheetDialog

@Composable
fun CreateProfileScreen(navController: NavController,viewModel: CreateProfileViewModel = hiltViewModel()) {

    CustomBottomSheetDialog(
        {viewModel.customBottomSheetDialogState.value.showDialog},
        {viewModel.customBottomSheetDialogState.value.items},
        viewModel.customBottomSheetDialogState.value.onClickCancel
    )
    Box(
      modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "createProfile",modifier = Modifier.align(Alignment.Center).clickable{ viewModel.showBottomSheetDialog()})
    }

}
