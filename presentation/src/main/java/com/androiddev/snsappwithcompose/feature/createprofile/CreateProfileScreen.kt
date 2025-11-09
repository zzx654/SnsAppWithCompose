package com.androiddev.snsappwithcompose.feature.createprofile

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.androiddev.snsappwithcompose.common.component.CustomBottomSheetDialog
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.feature.auth.components.BirthTextField
import com.androiddev.snsappwithcompose.feature.auth.components.BottomButton
import com.androiddev.snsappwithcompose.feature.createprofile.component.BottomWheelPicker
import com.androiddev.snsappwithcompose.common.component.EditProfileImage
import com.androiddev.snsappwithcompose.feature.createprofile.component.NicknameTextField
import com.androiddev.snsappwithcompose.common.util.decodeBase64
import java.util.Calendar
import android.Manifest
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.background
import com.androiddev.snsappwithcompose.ui.theme.LightGray
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.androiddev.snsappwithcompose.common.base.component.BaseScaffold
import com.androiddev.snsappwithcompose.common.component.AlertDialog
import com.androiddev.snsappwithcompose.feature.createprofile.component.GenderRadioButtons
import com.androiddev.snsappwithcompose.common.component.LoadingDialog
import com.androiddev.snsappwithcompose.common.state.UiEvent
import com.androiddev.snsappwithcompose.common.util.checkPermissions
import com.androiddev.snsappwithcompose.feature.createprofile.event.CreateProfileEvent
import kotlinx.coroutines.flow.collectLatest

@RequiresApi(Build.VERSION_CODES.N)
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
        navBackStackEntry.savedStateHandle.set<String>(getString(context,R.string.encodedBitmap),null)
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
        }
    }
    val photoPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()){ isGranted ->
        if(isGranted){
            galleryLauncher.launch("image/*")
        }
    }
    val launcherMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
        if (areGranted) {
            Log.d("permission", "권한이 동의되었습니다.")
        }
        else {
            Log.d("permission", "권한이 거부되었습니다.")
        }
    }
    LaunchedEffect(true) {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        checkPermissions(
            context,
            permissions,
            onUnGranted = {
                launcherMultiplePermissions.launch(permissions)
            }
        )
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).also {
                        it.setGravity(Gravity.BOTTOM, 0, 130)
                        it.show()
                    }
                }
                is UiEvent.navigate -> {
                    navController.navigate(event.screen)
                }
                else -> null
            }
        }
    }

    val year = Calendar.getInstance().get(Calendar.YEAR)
    viewModel.setLauncher({cameraPermission.launch(android.Manifest.permission.CAMERA)},{photoPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)})
    
    CustomBottomSheetDialog(
        { viewModel.customBottomSheetDialogState.value.showDialog },
        { viewModel.customBottomSheetDialogState.value.items },
        viewModel.customBottomSheetDialogState.value.onClickCancel
    )
    BottomWheelPicker(
        initValue = { viewModel.birthYear.value?: 2005 },
        min = year-70,
        max = year-15,
        pickerMaxHeight = 250,
        showDialog = { viewModel.bottomWheelState.value.showDialog },
        onClickConfirm = {
            viewModel.onEvent(CreateProfileEvent.SetBirthYear(it))
        },
        onClickCancel = viewModel.bottomWheelState.value.onClickCancel,
    )
    LoadingDialog { viewModel.isLoading.value }
    AlertDialog(
        title = {viewModel.alertDialogState.value.title},
        cancelText = {viewModel.alertDialogState.value.cancelText},
        confirmText = {viewModel.alertDialogState.value.confirmText},
        onClickConfirm = viewModel.alertDialogState.value.onClickConfirm,
        onClickCancel = viewModel.alertDialogState.value.onClickCancel,
        content = {
            Box(modifier = Modifier
                .width(160.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(color = LightGray),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "닉네임: ${viewModel.nickname.value}",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                        )
                    Text(
                        text = "성별: ${viewModel.gender.value}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "출생: ${viewModel.birthYear.value}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
    BaseScaffold(
        focusManager = focusManager,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = getString(context,R.string.createProfile),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ) },
            )
        },
        bottomBar = {
            BottomButton(
                buttonText = stringResource(id = R.string.request_signup),
                activeButton = {
                    viewModel.isNicknameValid.value
                            && viewModel.birthYear.value != null
                            && viewModel.gender.value.isNotBlank()
                },
                onClick = { viewModel.onEvent(CreateProfileEvent.ShowCreateProfileAlert)}
            )
        },
        content = {
            Spacer(modifier = Modifier.height(30.dp))
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                EditProfileImage(
                    profileBmap = {viewModel.profileBmap.value},
                    modifier = Modifier.align(Alignment.Center).clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() } // This is mandatory
                    ){
                        viewModel.onEvent(CreateProfileEvent.ShowProfileImageOptions)
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            NicknameTextField(
                modifier = Modifier.fillMaxWidth(),
                focusManager = focusManager,
                text = { viewModel.nickname.value },
                hint = getString(context,R.string.nickname_hint),
                onTextChange = {
                    viewModel.onEvent(CreateProfileEvent.TypeNickname(it))
                },
                isTyping = { viewModel.isTyping.value },
                isNicknameValid = { viewModel.isNicknameValid.value },
                isNicknameChecking = { viewModel.isNicknameChecking.value }
            )
            Spacer(modifier = Modifier.height(8.dp))
            BirthTextField(
                birth = { viewModel.birthYear.value },
                onClick = { viewModel.onEvent(CreateProfileEvent.ShowBirthYearOptions) }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                GenderRadioButtons(
                    listOf(getString(context,R.string.male),getString(context,R.string.female),getString(context,R.string.private_info)),
                    { viewModel.gender.value },
                    { viewModel.onEvent(CreateProfileEvent.SetGender(it)) }
                )
            }
        }
    )
}
