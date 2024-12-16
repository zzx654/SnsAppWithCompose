package com.androiddev.snsappwithcompose.createprofile

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.androiddev.snsappwithcompose.components.CustomBottomSheetDialog
import com.androiddev.snsappwithcompose.util.Screen
import com.canhub.cropper.CropImageContract

@Composable
fun CreateProfileScreen(navController: NavController,viewModel: CreateProfileViewModel = hiltViewModel()) {

    val rotateMatrix = Matrix().also{
        it.postRotate(90f)
    }
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()){ uri ->

        val encoded = Uri.encode(uri.toString())

        viewModel.setImageUri(uri)
        viewModel.setBitmapImage(null)
        navController.navigate(Screen.CropScreen(encoded))
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()){ bitmapImage ->
        bitmapImage?.let {
            viewModel.setBitmapImage(Bitmap.createBitmap(it, 0, 0,        it.getWidth(), it.getHeight(), rotateMatrix, false))
            viewModel.setImageUri(null)
        }
    }
    val cameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()){ isGranted ->
        if(isGranted){
            cameraLauncher.launch(null)
        }else {
        }
    }
    val photoPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()){ isGranted ->
        if(isGranted){
            galleryLauncher.launch("image/*")
        }else {
        }
    }
    viewModel.setLauncher({cameraPermission.launch(android.Manifest.permission.CAMERA)},{photoPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)})
    
    CustomBottomSheetDialog(
        {viewModel.customBottomSheetDialogState.value.showDialog},
        {viewModel.customBottomSheetDialogState.value.items},
        viewModel.customBottomSheetDialogState.value.onClickCancel
    )
    Column(
        modifier = Modifier.fillMaxSize(),

    ){

    }
    Box(
      modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "createProfile",modifier = Modifier
                .clickable { viewModel.showBottomSheetDialog() })
            if(viewModel.bitmapImage.value!=null||viewModel.imageUri.value !=null) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(viewModel.imageUri.value).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                    modifier = Modifier.fillMaxWidth().fillMaxWidth(0.8f)
                )
            }
            if(viewModel.bitmapImage.value!=null) {
                Image(
                    bitmap = viewModel.bitmapImage.value!!.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            }
        }

    }

}
