package com.androiddev.snsappwithcompose.feature.upload_post

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.view.Gravity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Photo
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
import androidx.navigation.NavController
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.component.CenterAlignedTopBar
import com.androiddev.snsappwithcompose.common.component.Chips
import com.androiddev.snsappwithcompose.common.component.CustomChip
import com.androiddev.snsappwithcompose.common.component.SearchTextField
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import com.androiddev.snsappwithcompose.common.base.component.BaseScaffold
import com.androiddev.snsappwithcompose.common.state.UiEvent
import com.androiddev.snsappwithcompose.common.util.checkPermissions
import com.androiddev.snsappwithcompose.common.util.fetchLocation
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.ui.Alignment
import com.androiddev.domain.model.PostPreview
import com.androiddev.snsappwithcompose.common.component.AlertDialog
import com.androiddev.snsappwithcompose.common.component.CustomBottomSheetDialog
import com.androiddev.snsappwithcompose.common.component.LoadingDialogWithText
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_AUDIO
import com.androiddev.snsappwithcompose.feature.upload_post.component.CheckBoxWithText
import com.androiddev.snsappwithcompose.feature.upload_post.component.ContentTextField
import com.androiddev.snsappwithcompose.feature.upload_post.component.SelectedImageCards
import com.androiddev.snsappwithcompose.feature.upload_post.component.SelectedMediaCards
import com.androiddev.snsappwithcompose.feature.upload_post.component.UploadRecordIcon
import com.androiddev.snsappwithcompose.feature.upload_post.component.UploadVoteIcon
import com.androiddev.snsappwithcompose.feature.upload_post.record.BottomRecorder
import com.androiddev.snsappwithcompose.feature.upload_post.record.RecordEvent
import com.androiddev.snsappwithcompose.feature.upload_post.record.RecordViewModel
import com.androiddev.snsappwithcompose.feature.upload_post.vote.BottomVoteOptions
import com.androiddev.snsappwithcompose.feature.upload_post.vote.CreateVoteEvent
import com.androiddev.snsappwithcompose.feature.upload_post.vote.CreateVoteViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RestrictedApi", "SuspiciousIndentation",
    "NewApi"
)
@Composable
fun UploadPostScreen(
    navController: NavController,
    post: PostPreview?,
    viewModel: UploadPostViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    recordViewModel: RecordViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    createVoteViewModel: CreateVoteViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()

) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val contentTextFieldState = rememberTextFieldState()


    //val formattedTime = "%02d:%02d".format(elapsed / 60, elapsed % 60)
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
            //viewModel.onEvent(UploadPostEvent.AddImages(uriList))
            viewModel.onEvent(UploadPostEvent.AddMedia(uriList))
        }
    )
    val launcherMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        //val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
    }
    LoadingDialogWithText(
        text = getString(context,R.string.uploading_alert),
        isLoading = { viewModel.isLoading.value}
    )
    LaunchedEffect(Unit) {

        post?.let {
            viewModel.initPost(
                post = it
            )
            it.vote?.let {
                createVoteViewModel.initVoteState()
            }
            it.media.firstOrNull { it.type == MEDIA_TYPE_AUDIO }?.let { media ->

                recordViewModel.initRecordState(remotePath = media.url)
            }

            //it.audio?.let{ remotePath ->
             //   recordViewModel.initRecordState(remotePath = remotePath)
            //}

            contentTextFieldState.edit {
                replace(0, contentTextFieldState.text.toString().length, it.text)
            }


        }
    }
    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).also {
                        it.setGravity(Gravity.BOTTOM, 0, 130)
                        it.show()
                    }
                }

                is UiEvent.navigate -> {
                    navController.navigate(event.screen)
                }

                UiEvent.popBackStack -> {
                    navController.popBackStack()
                }
                is UiEvent.PopBackStackWithResult<*> -> {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(event.key, event.value)
                    navController.popBackStack()
                }
            }
        }
    }
    LaunchedEffect(true) {
        recordViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).also {
                        it.setGravity(Gravity.BOTTOM, 0, 130)
                        it.show()
                    }
                }
                else -> null
            }
        }
    }
    LaunchedEffect(true) {
        createVoteViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).also {
                        it.setGravity(Gravity.BOTTOM, 0, 130)
                        it.show()
                    }
                }
                else -> null
            }
        }
    }
    LaunchedEffect(contentTextFieldState.text) {
        viewModel.onEvent(UploadPostEvent.TypeContent(contentTextFieldState.text.toString()))
    }
    CustomBottomSheetDialog(
        { createVoteViewModel.manageVoteDialogState.value.showDialog },
        { createVoteViewModel.manageVoteDialogState.value.items },
        createVoteViewModel.manageVoteDialogState.value.onClickCancel
    )
    BottomRecorder(
      showDialog = { recordViewModel.bottomRecordDialogState.value.showDialog },
      onClickCancel = recordViewModel.bottomRecordDialogState.value.onClickCancel,
      onClickSave = {
          recordViewModel.onEvent(RecordEvent.SaveRecording)

                    }  ,
      viewModel = recordViewModel
    )
    BottomVoteOptions(
        createVoteViewModel
    )
    AlertDialog(
        title = { recordViewModel.recordingAlertDialogState.value.title },
        cancelText = { recordViewModel.recordingAlertDialogState.value.cancelText },
        confirmText = { recordViewModel.recordingAlertDialogState.value.confirmText },
        onClickConfirm = recordViewModel.recordingAlertDialogState.value.onClickConfirm,
        onClickCancel = recordViewModel.recordingAlertDialogState.value.onClickCancel
    )
    BaseScaffold(
        focusManager = focusManager,
        topBar = {
            CenterAlignedTopBar(
                title = stringResource(R.string.upload_post),
                onBackClick = { navController.popBackStack() },
                rightAction = {
                    IconButton(onClick = {
                        if (viewModel.locationOnOff.value) {
                            checkPermissions(
                                context = context,
                                permissions = arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ),
                                onGranted = {
                                    fetchLocation(fusedLocationClient) { latitude, longitude ->
                                        viewModel.onEvent(
                                            UploadPostEvent.UploadPost(
                                                lat = latitude,
                                                long = longitude,
                                                audioFilePath = recordViewModel.recordedFilePath.value,
                                                deleteAudio = recordViewModel.deletedAudio,
                                                voteOptions = createVoteViewModel.savedVoteOptions
                                            )
                                        )
                                    }
                                },
                                onUnGranted = {
                                    //viewModel.onEvent(UploadPostEvent.SetLocationOnOff(false))
                                }
                            )
                        } else {
                            viewModel.onEvent(UploadPostEvent.UploadPost(
                                audioFilePath = recordViewModel.recordedFilePath.value,
                                voteOptions = createVoteViewModel.savedVoteOptions
                            ))
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
        bottomBar = {
            Surface(shadowElevation = 10.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.DarkGray.copy(0.35f)),
                ) {
                    Spacer(modifier = Modifier.width(5.dp))
                    IconButton(
                        modifier = Modifier.size(58.dp),
                        onClick = {
                            imageLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageAndVideo
                                )
                            )
                        }
                    ) {
                        Icon(
                            Icons.Default.Photo,
                            contentDescription = null,
                        )
                    }

                    IconButton(
                        modifier = Modifier.size(58.dp),
                        onClick = {
                            checkPermissions(
                                context = context,
                                permissions = arrayOf(
                                    Manifest.permission.RECORD_AUDIO
                                ),
                                onGranted = {
                                    recordViewModel.onEvent(RecordEvent.OnAddRecordClick)
                                },
                                onUnGranted = {
                                    launcherMultiplePermissions.launch(
                                        arrayOf(
                                            Manifest.permission.RECORD_AUDIO
                                        )
                                    )
                                }
                            )
                        },

                    ) {
                        UploadRecordIcon(recordViewModel.recorded.value) { }
                    }

                    IconButton(
                        modifier = Modifier.size(58.dp),
                        onClick = { createVoteViewModel.onEvent(CreateVoteEvent.OnAddVoteClick(postMode = viewModel.postMode?: PostMode.CREATE))}
                    ) {
                        UploadVoteIcon(createVoteViewModel.saved.value) { }
                    }
                    IconButton(
                        modifier = Modifier.size(58.dp),
                        onClick = {
                            checkPermissions(
                                context = context,
                                permissions = arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ),
                                onGranted = {
                                    viewModel.onEvent(
                                        UploadPostEvent.ToggleLocationOnOff(
                                            !viewModel.locationOnOff.value
                                        )
                                    )
                                },
                                onUnGranted = {
                                    launcherMultiplePermissions.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                    )
                                    viewModel.onEvent(UploadPostEvent.SetLocationOnOff(false))
                                }
                            )
                        }
                    ) {
                        Icon(
                            imageVector = if (viewModel.locationOnOff.value) Icons.Default.LocationOn else Icons.Default.LocationOff,
                            contentDescription = null,
                        )
                    }
                }
            }
        },
        scrollState = scrollState,
        content = {
            Spacer(modifier = Modifier.height(30.dp))
            Chips(
                modifier = Modifier.fillMaxWidth(),
                list = viewModel.addedTags.map{ "#"+it },
                chip = { data: String, index: Int ->
                    CustomChip(
                        backgroundColor = Color.Gray,
                        text = data,
                        onDeleteClick = {
                            viewModel.onEvent(UploadPostEvent.DeleteTag(data))
                                        },
                        border = true
                    )
                }
            )
            SearchTextField(
                modifier = Modifier.fillMaxWidth(),
                text = { viewModel.tagTextField.value },
                onTextChange = { viewModel.onEvent(UploadPostEvent.TypeTag(it)) },
                hint = getString(context, R.string.searchtag_hint)
            )

            Chips(
                modifier = Modifier.fillMaxWidth(),
                list = viewModel.searchedTags.map { "${it.tagname}(${it.tagcount})" },
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
                state = contentTextFieldState,
                scrollState = scrollState,
                hint = getString(context, R.string.uploadtext_hint)

            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth(),contentAlignment = Alignment.TopStart){
                CheckBoxWithText(
                    text = getString(context, R.string.anonymous),
                    checked = { viewModel.anonymous.value },
                    onCheckedChange = { viewModel.onEvent(UploadPostEvent.ToggleCheckBox(it)) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            SelectedMediaCards(
                selectedMedia = {
                    viewModel.selectedMediaItems
                },
                onClickItem = { navController.navigate(Screen.MediaPreviewScreen)},
                onDeleteClick = {
                    viewModel.onEvent(UploadPostEvent.DeleteMedia(it))
                }
            )

            /**SelectedImageCards(
                selectedImages = {
                    viewModel.selectedImages
                },
                onDeleteClick = {
                    viewModel.onEvent(UploadPostEvent.DeleteImage(it))
                }
            )**/
        }
    )
}