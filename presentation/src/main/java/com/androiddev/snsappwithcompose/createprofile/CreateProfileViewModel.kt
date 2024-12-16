package com.androiddev.snsappwithcompose.createprofile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.BottomSheetItem
import com.androiddev.snsappwithcompose.util.CustomBottomSheetDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(private val context: Context): ViewModel() {
    val _customBottomSheetDialogState: MutableState<CustomBottomSheetDialogState> = mutableStateOf(
        CustomBottomSheetDialogState()
    )
    val customBottomSheetDialogState: State<CustomBottomSheetDialogState>
        get() = _customBottomSheetDialogState

    val _imageUri: MutableState<Uri?> = mutableStateOf(null)
    val imageUri:State<Uri?>
        get() = _imageUri
    val _bitmapImage:MutableState<Bitmap?> = mutableStateOf(null)
    val bitmapImage:State<Bitmap?>
        get() = _bitmapImage
    var launchCamera:()->Unit = {}
    var launchGallery:()->Unit = {}
    fun setLauncher(cameraLauncher:()->Unit,galleryLauncher:()->Unit) {
       launchCamera = cameraLauncher
       launchGallery = galleryLauncher
    }
    fun setImageUri(uri:Uri?) {
        _imageUri.value = uri
    }
    fun setBitmapImage(bitmap:Bitmap?) {
        _bitmapImage.value = bitmap
    }
    fun showBottomSheetDialog() {
        _customBottomSheetDialogState.value = CustomBottomSheetDialogState(
            showDialog = true,
            listOf(
                BottomSheetItem(R.drawable.photo_library,getString(context,R.string.choose_from_gallery)) {
                    resetBottomSheetDialogState()
                    launchGallery()
                },
                BottomSheetItem(R.drawable.camera_outlined,getString(context,R.string.take_picture)) {
                    resetBottomSheetDialogState()
                    launchCamera()

                })
        ) { resetBottomSheetDialogState() }
    }
    private fun resetBottomSheetDialogState() {
        _customBottomSheetDialogState.value = CustomBottomSheetDialogState()
    }
}