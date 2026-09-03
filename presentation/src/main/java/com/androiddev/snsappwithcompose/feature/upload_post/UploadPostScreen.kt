package com.androiddev.snsappwithcompose.feature.upload_post

import android.Manifest
import android.annotation.SuppressLint
import android.view.Gravity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.util.checkPermissions
import com.androiddev.snsappwithcompose.common.util.fetchLocation
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androiddev.domain.model.MediaType
import com.androiddev.domain.model.Post
import com.androiddev.domain.model.PostPreview
import com.androiddev.snsappwithcompose.common.base.BaseScreen
import com.androiddev.snsappwithcompose.common.component.AlertDialog
import com.androiddev.snsappwithcompose.common.component.CustomBottomSheetDialog
import com.androiddev.snsappwithcompose.common.component.LoadingDialogWithText
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.util.Constants.MEDIA_TYPE_AUDIO
import com.androiddev.snsappwithcompose.common.util.MediaItemFactory
import com.androiddev.snsappwithcompose.common.util.rememberMediaPicker
import com.androiddev.snsappwithcompose.feature.upload_post.component.CheckBoxWithText
import com.androiddev.snsappwithcompose.feature.upload_post.component.ContentTextField
import com.androiddev.snsappwithcompose.feature.upload_post.component.MediaItem
import com.androiddev.snsappwithcompose.feature.upload_post.component.SelectedMediaCards
import com.androiddev.snsappwithcompose.feature.upload_post.component.UploadRecordIcon
import com.androiddev.snsappwithcompose.feature.upload_post.component.UploadVoteIcon
import com.androiddev.snsappwithcompose.feature.upload_post.record.BottomRecorder
import com.androiddev.snsappwithcompose.feature.upload_post.record.RecordEvent
import com.androiddev.snsappwithcompose.feature.upload_post.record.RecordViewModel
import com.androiddev.snsappwithcompose.feature.upload_post.util.getVideoThumbnail
import com.androiddev.snsappwithcompose.feature.upload_post.util.isVideo
import com.androiddev.snsappwithcompose.feature.upload_post.vote.BottomVoteOptions
import com.androiddev.snsappwithcompose.feature.upload_post.vote.CreateVoteEvent
import com.androiddev.snsappwithcompose.feature.upload_post.vote.CreateVoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RestrictedApi", "SuspiciousIndentation",
    "NewApi"
)
@Composable
fun UploadPostScreen(
    navController: NavController,
    viewModel: UploadPostViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    recordViewModel: RecordViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    createVoteViewModel: CreateVoteViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()

) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val postMode by viewModel.postMode.collectAsStateWithLifecycle()
    val contentTextFieldState = rememberTextFieldState()

    val cachedText by viewModel.cachedText.collectAsStateWithLifecycle()

    val cachedVote by viewModel.cachedVote.collectAsStateWithLifecycle()

    val cachedAudio by viewModel.cachedAudio.collectAsStateWithLifecycle()


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val searchTagUiState by viewModel.searchTagUiState.collectAsStateWithLifecycle()

    val mediaItemFactory = remember(context) { MediaItemFactory(context) }
    val launchMediaPicker = rememberMediaPicker { uris ->
        scope.launch {
            val mediaItems = mediaItemFactory.createMediaItems(uris)
            viewModel.onEvent(UploadPostEvent.AddMedia(mediaItems))
        }
    }
    val launcherMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        //val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
    }

    LaunchedEffect(Unit) {
        if(postMode == PostMode.CREATE) {
            checkPermissions(
                context = context,
                permissions = arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                onGranted = { viewModel.setLocationOnOff(true) },
                onUnGranted = { viewModel.setLocationOnOff(false) }
            )
        }
    }
    LaunchedEffect(cachedVote) {
        if(cachedVote!=null)
            createVoteViewModel.initVoteState()
    }
    LaunchedEffect(cachedText) {
        if(cachedText.isNotEmpty()) {
            contentTextFieldState.edit {
                replace(0, contentTextFieldState.text.toString().length, cachedText)
            }
        }
    }
    LaunchedEffect(cachedAudio) {
        cachedAudio?.let {
            recordViewModel.initRecordState(remotePath = it)
        }
    }
    LaunchedEffect(true) {
        recordViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).also {
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
                    Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).also {
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
    BaseScreen(
        viewModel = viewModel,
        navController = navController,
        loadingContent = {
            LoadingDialogWithText(
                text = stringResource(R.string.uploading_alert),
                isLoading = { viewModel.isLoading.value }
            )
        }
    ) {
        BaseScaffold(
            focusManager = focusManager,
            topBar = {
                CenterAlignedTopBar(
                    title = stringResource(R.string.upload_post),
                    onBackClick = { navController.popBackStack() },
                    rightAction = {
                        IconButton(onClick = {
                            viewModel.onEvent(UploadPostEvent.UploadPost(
                                audioFilePath = recordViewModel.recordedFilePath.value,
                                voteOptions = createVoteViewModel.savedVoteOptions,
                                deletedAudio = recordViewModel.deletedAudio
                            ))

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
                            onClick = launchMediaPicker
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
                            onClick = { createVoteViewModel.onEvent(CreateVoteEvent.OnAddVoteClick(postMode = postMode))}
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
                                            UploadPostEvent.ToggleLocationOnOff
                                        )
                                    },
                                    onUnGranted = {
                                        launcherMultiplePermissions.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                                Manifest.permission.ACCESS_FINE_LOCATION
                                            )
                                        )
                                        viewModel.setLocationOnOff(false)
                                    }
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (uiState.isLocationOn) Icons.Default.LocationOn else Icons.Default.LocationOff,
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
                    list = searchTagUiState.addedTags.map { "#$it" },
                    chip = { data: String, _: Int ->
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
                    text = { searchTagUiState.tagText },
                    onTextChange = { viewModel.onEvent(UploadPostEvent.TypeTag(it)) },
                    hint = getString(context, R.string.searchtag_hint)
                )

                Chips(
                    modifier = Modifier.fillMaxWidth(),
                    list = searchTagUiState.searchedTags.map { "${it.tagname}(${it.tagcount})" },
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
                        text = stringResource(R.string.anonymous),
                        checked = { uiState.isAnonymous },
                        onCheckedChange = { viewModel.onEvent(UploadPostEvent.ToggleCheckBox(it)) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                SelectedMediaCards(
                    selectedMedia = {
                        uiState.selectedMediaItems
                    },
                    onClickItem = { navController.navigate(Screen.MediaEditScreen)},
                    onDeleteClick = {
                        viewModel.onEvent(UploadPostEvent.DeleteMedia(it))
                    }
                )
            }
        )

    }

}