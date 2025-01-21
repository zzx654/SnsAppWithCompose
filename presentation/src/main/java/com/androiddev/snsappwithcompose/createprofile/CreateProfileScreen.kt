package com.androiddev.snsappwithcompose.createprofile

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.components.CustomBottomSheetDialog
import com.androiddev.snsappwithcompose.util.Screen
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.auth.components.BottomButton
import com.androiddev.snsappwithcompose.components.EditProfileImage
import com.androiddev.snsappwithcompose.components.NicknameTextField
import com.androiddev.snsappwithcompose.util.addFocusCleaner
import com.androiddev.snsappwithcompose.util.decodeBase64

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
    viewModel: CreateProfileViewModel = hiltViewModel()
) {

    val rotateMatrix = Matrix().also{
        it.postRotate(90f)
    }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val encodedCroppedBitmap = navBackStackEntry.savedStateHandle.get<String>(getString(context,R.string.encodedBitmap))
    encodedCroppedBitmap?.let {
        viewModel.setProfileBmap(decodeBase64(it))
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()){ uri ->
        val encoded = Uri.encode(uri.toString())
        navController.navigate(Screen.CropScreen(encoded))
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()){ bitmapImage ->
        bitmapImage?.let {
            viewModel.setProfileBmap(Bitmap.createBitmap(it, 0, 0,it.getWidth(), it.getHeight(), rotateMatrix, false))
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
        { viewModel.customBottomSheetDialogState.value.showDialog },
        { viewModel.customBottomSheetDialogState.value.items },
        viewModel.customBottomSheetDialogState.value.onClickCancel
    )
    Scaffold(

        topBar = {
            Surface(shadowElevation = 3.dp) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "프로필 작성",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        ) },
                )
            }
        },
        modifier = Modifier.fillMaxSize().addFocusCleaner(focusManager)
    ) { contentPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()

        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .padding(contentPadding)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                EditProfileImage(
                    profileBmap = viewModel.profileBmap.value,
                    modifier = Modifier.align(Alignment.CenterHorizontally).clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() } // This is mandatory
                    ){viewModel.showBottomSheetDialog() }
                )
                Spacer(modifier = Modifier.height(50.dp))

                NicknameTextField(
                    modifier = Modifier.fillMaxWidth(),
                    focusManager = focusManager,
                    text = { viewModel.nickname.value },
                    hint = "닉네임",
                    onTextChange = {
                        viewModel.onEvent(CreateProfileEvent.TypeNickname(it))
                    },
                    isTyping = { viewModel.isTyping.value },
                    isNicknameValid = { viewModel.isNicknameValid.value }
                )
                Spacer(modifier = Modifier.height(50.dp))
                //NicknameHelper(
                  //  isTyping = { viewModel.isTyping.value },
                   // isNicknameValid = { viewModel.isNicknameValid.value },
                    //nickname = { viewModel.nickname.value }

                //)

            }
            BottomButton(
                buttonText = stringResource(id = R.string.request_signup),
                activeButton = {
                    true
                },
                onClick = { viewModel.onEvent(CreateProfileEvent.uploadImage)}
            )
        }
    }
}
