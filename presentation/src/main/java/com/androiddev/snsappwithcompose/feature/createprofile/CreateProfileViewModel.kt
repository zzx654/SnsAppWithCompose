package com.androiddev.snsappwithcompose.feature.createprofile

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewModelScope
import com.androiddev.data.util.getImageUri
import com.androiddev.domain.use_case.createprofile.CreateProfileUseCases
import com.androiddev.domain.util.Resource
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.state.AlertDialogState
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.data.util.getMultipartBody
import com.androiddev.domain.model.MediaType
import com.androiddev.snsappwithcompose.common.state.AlertDialogStateV2
import com.androiddev.snsappwithcompose.common.state.BottomSheetDialogState
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.feature.createprofile.event.CreateProfileEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val createProfileUseCases: CreateProfileUseCases,
): BaseViewModel() {
    private val _bottomSheetDialogState =  MutableStateFlow(BottomSheetDialogState<CreateProfileImageOption>())
    val bottomSheetDialogState = _bottomSheetDialogState.asStateFlow()


    private val _bottomWheelDialogState = MutableStateFlow(BottomWheelState())
    val bottomWheelState: StateFlow<BottomWheelState>
        get() = _bottomWheelDialogState.asStateFlow()
    private val _profileBmap:MutableStateFlow<Bitmap?> = MutableStateFlow(null)
    val profileBmap:StateFlow<Bitmap?>
        get() = _profileBmap.asStateFlow()

    private val _isNicknameChecking = MutableStateFlow(false)
    val isNicknameChecking: StateFlow<Boolean>
        get() = _isNicknameChecking.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean>
        get() = _isTyping.asStateFlow()
    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String>
        get() = _nickname.asStateFlow()

    private val _isNicknameValid = MutableStateFlow(false)
    val isNicknameValid: StateFlow<Boolean>
        get() = _isNicknameValid.asStateFlow()

    private val _birthYear = MutableStateFlow<Int?>(null)
    val birthYear: StateFlow<Int?>
        get() = _birthYear.asStateFlow()

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String>
        get() = _gender.asStateFlow()
    private val _alertDialogState = MutableStateFlow(AlertDialogStateV2())
    val alertDialogState: StateFlow<AlertDialogStateV2>
        get() = _alertDialogState.asStateFlow()
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
            is CreateProfileEvent.ShowProfileImageOptions -> {
                showBottomSheetDialog()
            }
            is CreateProfileEvent.ShowBirthYearOptions -> {
                showBottomWheelDialog()
            }
            is CreateProfileEvent.SetGender -> {
                _gender.value = event.gender
            }
            is CreateProfileEvent.SetBirthYear -> {
                _birthYear.value = event.birthYear
                resetBottomWheelDialogState()
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
                        createProfileUseCases.checkNickname(event.nickname)
                            .collect { result ->
                                when(result) {

                                    is Resource.Success -> {
                                        _isNicknameChecking.value = false
                                        result.data?.let {
                                            _isNicknameValid.value = it.isValid
                                        }
                                    }
                                    is Resource.Error -> {
                                        _isNicknameChecking.value = false
                                    }
                                    is Resource.Loading -> {
                                        _isNicknameChecking.value = true
                                    }

                                    is Resource.TokenExpired -> {
                                        setEvent(
                                            UiEvent.navigate(
                                                screen = Screen.SignInScreen
                                            )
                                        )
                                    }
                                }
                            }
                    }
                } else {
                    _isNicknameValid.value = false
                }
            }
            is CreateProfileEvent.ShowCreateProfileAlert -> {
                showCreateProfileAlert(event.imageUri)
            }
        }
    }
    private fun showBottomSheetDialog() {
        val options = if(profileBmap.value!=null) {
            listOf(CreateProfileImageOption.Camera,CreateProfileImageOption.Gallery,CreateProfileImageOption.Delete)
        } else {
            listOf(CreateProfileImageOption.Camera,CreateProfileImageOption.Gallery)
        }

        _bottomSheetDialogState.value = BottomSheetDialogState(
            showDialog = true,
            options = options,
            onOptionSelected = { option ->
                resetBottomSheetDialogState()
                handleProfileImageOption(option)
            },
            onClickCancel = { resetBottomSheetDialogState() }
        )

    }
    private fun handleProfileImageOption(option: CreateProfileImageOption) {
        when (option) {
            CreateProfileImageOption.Camera -> {
                resetBottomSheetDialogState()
                launchCamera()
            }
            CreateProfileImageOption.Gallery -> {
                resetBottomSheetDialogState()
                launchGallery()
            }
            CreateProfileImageOption.Delete -> {
                resetBottomSheetDialogState()
                _profileBmap.value = null
            }
        }
    }
    private fun resetBottomSheetDialogState() {
        _bottomSheetDialogState.value = BottomSheetDialogState()
    }
    private fun showBottomWheelDialog() {
        _bottomWheelDialogState.value = BottomWheelState(
            showDialog = true,
            onClickCancel = { resetBottomWheelDialogState() }
        )
    }
    private fun resetBottomWheelDialogState() {
        _bottomWheelDialogState.value = BottomWheelState()
    }
    private fun showCreateProfileAlert(imageUri: Uri?) {
        _alertDialogState.value = AlertDialogStateV2(
            title = UiText.StringResource(R.string.check_profile),
            cancelText = UiText.StringResource(R.string.cancel),
            confirmText = UiText.StringResource(R.string.confirm),
            onClickCancel = {
                resetDialogState()
            },
            onClickConfirm = {
                viewModelScope.launch {
                    createProfileUseCases.createProfile(imageUri,nickname.value,birthYear.value?:2005,gender.value)
                        .collect { result ->

                            handleResource(
                                resource = result,
                                onSuccessUnit = {
                                    setEvent(
                                        UiEvent.navigate(
                                            screen = Screen.MainScreen
                                        )
                                    )

                                }
                            )
                        }
                }
                resetDialogState()
            }
        )
    }

    protected fun resetDialogState() {
        _alertDialogState.value = AlertDialogStateV2()
    }
}