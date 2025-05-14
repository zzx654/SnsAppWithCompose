package com.androiddev.snsappwithcompose.upload_post

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.view.Gravity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.components.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.components.CheckBoxWithText
import com.androiddev.snsappwithcompose.components.Chips
import com.androiddev.snsappwithcompose.components.ContentTextField
import com.androiddev.snsappwithcompose.components.CustomChip
import com.androiddev.snsappwithcompose.components.ScreenWithTopBar
import com.androiddev.snsappwithcompose.components.SearchTextField
import com.androiddev.snsappwithcompose.components.SelectedImageCards
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import com.androiddev.snsappwithcompose.util.UiEvent
import com.androiddev.snsappwithcompose.util.checkPermissions
import com.androiddev.snsappwithcompose.util.fetchLocation
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RestrictedApi")
@Composable
fun UploadPostScreen(navController: NavController,viewModel: UploadPostViewModel = hiltViewModel()) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var selectedImageUriList by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uriList ->
            selectedImageUriList = uriList
            viewModel.onEvent(UploadPostEvent.AddImages(uriList))
        }
    )
    val launcherMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
    }
    LaunchedEffect(true) {
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
            }
        }
    }
    ScreenWithTopBar(
        topBar = {
            CenterAlignedTopBar(
                title = stringResource(R.string.upload_post),
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton( onClick = {
                        if(viewModel.locationOnOff.value) {
                            checkPermissions(
                                context = context,
                                permissions = arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION),
                                onGranted = {
                                    fetchLocation(fusedLocationClient) { latitude,longitude ->
                                        viewModel.onEvent(
                                            UploadPostEvent.UploadPost(
                                                lat = latitude,
                                                long = longitude
                                            )
                                        )
                                    }
                                },
                                onUnGranted = {
                                    //viewModel.onEvent(UploadPostEvent.SetLocationOnOff(false))
                                }
                            )
                        } else {
                            viewModel.onEvent(UploadPostEvent.UploadPost())
                        }

                    }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        focusManager = focusManager,
        content =  {

            Spacer(modifier = Modifier.height(30.dp))
            Chips(
                modifier = Modifier.fillMaxWidth(),
                list = viewModel.addedTags,
                chip = { data: String, index: Int ->
                    CustomChip(
                        backgroundColor = Color.Gray,
                        text = data,
                        onDeleteClick = { viewModel.onEvent(UploadPostEvent.DeleteTag(data))},
                        border = true
                    )
                }
            )
            SearchTextField(
                modifier = Modifier.fillMaxWidth(),
                text = { viewModel.tagTextField.value },
                onTextChange = { viewModel.onEvent(UploadPostEvent.TypeTag(it))},
                hint = getString(context,R.string.searchtag_hint)
            )

            Chips(
                modifier = Modifier.fillMaxWidth(),
                list = viewModel.searchedTags.map { "${it.tagname}(${it.count})" },
                chip = { data: String, index: Int ->
                    CustomChip(
                        backgroundColor = Color.Gray,
                        text = data,
                        onChipClicked = { viewModel.onEvent(UploadPostEvent.AddTag(index)) },
                        border = true
                    )
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            ContentTextField(
                text = { viewModel.contentTextField.value },
                onTextChange = { viewModel.onEvent(UploadPostEvent.TypeContent(it))},
                hint = getString(context,R.string.uploadtext_hint)
            )
            Spacer(modifier = Modifier.height(10.dp))
            CheckBoxWithText(
              text = getString(context,R.string.anonymous),
              checked = { viewModel.anonymous.value },
              onCheckedChange = { viewModel.onEvent(UploadPostEvent.ToggleCheckBox(it))}
            )
            Spacer(modifier = Modifier.height(10.dp))
            SelectedImageCards(
                selectedImageUris = {
                    viewModel.selectedImages
                },
                onDeleteClick = {
                    viewModel.onEvent(UploadPostEvent.DeleteImage(it))
                }
            )
        },
        bottomBar = {//이미지,마이크,투표,위치
            Surface(shadowElevation = 10.dp) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.DarkGray.copy(0.2f)),
                    ){
                    Spacer(modifier = Modifier.width(5.dp))
                    IconButton(
                        modifier = Modifier.size(58.dp),
                        onClick = { imageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                    ) {
                        Icon(
                            Icons.Default.Photo,
                            contentDescription = null,
                            )
                    }
                    IconButton(
                        modifier = Modifier.size(58.dp),
                        onClick = { /* do something */ }
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = null,
                            )
                    }
                    IconButton(
                        modifier = Modifier.size(58.dp),
                        onClick = { /* do something */ }
                    ) {
                        Icon(
                            Icons.Default.HowToVote,
                            contentDescription = null,
                            )
                    }
                    IconButton(
                        modifier = Modifier.size(58.dp),
                        onClick = {
                            checkPermissions(
                                context = context,
                                permissions = arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION),
                                onGranted = { viewModel.onEvent(UploadPostEvent.ToggleLocationOnOff(!viewModel.locationOnOff.value))},
                                onUnGranted = {
                                    launcherMultiplePermissions.launch(arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION))
                                    viewModel.onEvent(UploadPostEvent.SetLocationOnOff(false))
                                }
                            )
                        }
                    ) {
                        Icon(
                            imageVector = if(viewModel.locationOnOff.value) Icons.Default.LocationOn else Icons.Default.LocationOff,
                            contentDescription = null,
                            )
                    }
                }
            }
        }
    )
}