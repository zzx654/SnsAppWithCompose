package com.androiddev.snsappwithcompose.createprofile

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddev.snsappwithcompose.util.getImageUri
import com.androiddev.domain.use_case.CreateProfileUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.util.BottomSheetItem
import com.androiddev.snsappwithcompose.util.CustomBottomSheetDialogState
import com.androiddev.snsappwithcompose.util.getMultipartBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val createProfileUseCases: CreateProfileUseCases,
    private val context: Context
): ViewModel() {
    val _customBottomSheetDialogState: MutableState<CustomBottomSheetDialogState> = mutableStateOf(
        CustomBottomSheetDialogState()
    )
    val customBottomSheetDialogState: State<CustomBottomSheetDialogState>
        get() = _customBottomSheetDialogState

    val _profileBmap:MutableState<Bitmap?> = mutableStateOf(null)
    val profileBmap:State<Bitmap?>
        get() = _profileBmap

    val _imageUrl:MutableState<String?> = mutableStateOf(null)
    val imageUrl:State<String?>
        get() = _imageUrl

    private val _isLoading = mutableStateOf(false)
    val isLoading : State<Boolean>
        get() = _isLoading

    private val _isTyping = mutableStateOf(false)
    val isTyping: State<Boolean>
        get() = _isTyping
    private val _nickname = mutableStateOf("")
    val nickname: State<String>
        get() = _nickname

    private val _isNicknameValid = mutableStateOf(false)
    val isNicknameValid: State<Boolean>
        get() = _isNicknameValid

    var launchCamera:()->Unit = {}
    var launchGallery:()->Unit = {}
    fun setLauncher(cameraLauncher:()->Unit,galleryLauncher:()->Unit) {
       launchCamera = cameraLauncher
       launchGallery = galleryLauncher
    }

    fun setProfileBmap(bitmap:Bitmap?) {
        _profileBmap.value = bitmap
    }
    fun onEvent(event: CreateProfileEvent) {
        when(event) {
            is CreateProfileEvent.uploadImage -> {
                viewModelScope.launch {

                    profileBmap.value?.let { bmap ->
                        val requestBody = getMultipartBody(getImageUri(context,bmap),context)
                        createProfileUseCases.uploadImage(requestBody)
                            .collect { result ->
                                when(result) {
                                    is Resource.Success -> {
                                        result.data?.let { it ->
                                            _imageUrl.value = it.imageUrl
                                        }
                                    }
                                    is Resource.Error -> {
                                        _isLoading.value = false
                                    }
                                    is Resource.Loading -> {
                                            _isLoading.value = true
                                    }
                                }
                            }
                    }
                }
            }
            is CreateProfileEvent.TypeNickname -> {
                _nickname.value = event.nickname
                _isTyping.value = true

                viewModelScope.launch {
                    delay(1000)
                    _isTyping.value = false
                }
                if(event.nickname.length >= 2) {
                    viewModelScope.launch {
                        delay(500L)
                        //검색 수행하고 결과에 따라 isNicknameValid 변경
                    }
                } else {
                    _isNicknameValid.value = false
                }
            }
        }
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