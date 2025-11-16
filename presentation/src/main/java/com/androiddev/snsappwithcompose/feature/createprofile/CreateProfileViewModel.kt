package com.androiddev.snsappwithcompose.feature.createprofile

import android.content.Context
import android.graphics.Bitmap
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
import com.androiddev.snsappwithcompose.common.base.viewmodel.BaseViewModel
import com.androiddev.snsappwithcompose.common.model.BottomSheetItem
import com.androiddev.snsappwithcompose.common.state.CustomBottomSheetDialogState
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.state.UiEvent
import com.androiddev.data.util.getMultipartBody
import com.androiddev.snsappwithcompose.feature.createprofile.event.CreateProfileEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val createProfileUseCases: CreateProfileUseCases,
    @ApplicationContext context: Context,
): BaseViewModel(context) {
    private val _customBottomSheetDialogState: MutableState<CustomBottomSheetDialogState> = mutableStateOf(
        CustomBottomSheetDialogState()
    )
    val customBottomSheetDialogState: State<CustomBottomSheetDialogState>
        get() = _customBottomSheetDialogState

    private val _bottomWheelDialogState: MutableState<BottomWheelState> = mutableStateOf(
        BottomWheelState()
    )
    val bottomWheelState: State<BottomWheelState>
        get() = _bottomWheelDialogState
    private val _profileBmap:MutableState<Bitmap?> = mutableStateOf(null)
    val profileBmap:State<Bitmap?>
        get() = _profileBmap

    private val _isNicknameChecking = mutableStateOf(false)
    val isNicknameChecking: State<Boolean>
        get() = _isNicknameChecking

    private val _isTyping = mutableStateOf(false)
    val isTyping: State<Boolean>
        get() = _isTyping
    private val _nickname = mutableStateOf("")
    val nickname: State<String>
        get() = _nickname

    private val _isNicknameValid = mutableStateOf(false)
    val isNicknameValid: State<Boolean>
        get() = _isNicknameValid

    private val _birthYear = mutableStateOf<Int?>(null)
    val birthYear: State<Int?>
        get() = _birthYear

    private val _gender = mutableStateOf("")
    val gender: State<String>
        get() = _gender
    private val _alertDialogState: MutableState<AlertDialogState> = mutableStateOf(AlertDialogState())
    val alertDialogState: State<AlertDialogState>
        get() = _alertDialogState
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
                showCreateProfileAlert()
            }
        }
    }
    private fun showBottomSheetDialog() {
        val items: MutableList<BottomSheetItem> = mutableListOf(
            BottomSheetItem(R.drawable.camera_outlined,getString(context,R.string.take_picture)) {
                resetBottomSheetDialogState()
                launchCamera()

            },
            BottomSheetItem(R.drawable.photo_library,getString(context,R.string.choose_from_gallery)) {
                resetBottomSheetDialogState()
                launchGallery()
            }
        )
        profileBmap.value?.let {
            items.add(
                BottomSheetItem(R.drawable.delete,getString(context,R.string.delete_profileimage)){
                    resetBottomSheetDialogState()
                    _profileBmap.value = null
                }
            )
        }
        _customBottomSheetDialogState.value = CustomBottomSheetDialogState(
            showDialog = true,
            items,
        ) { resetBottomSheetDialogState() }
    }
    private fun resetBottomSheetDialogState() {
        _customBottomSheetDialogState.value = CustomBottomSheetDialogState()
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
    private fun showCreateProfileAlert() {
        _alertDialogState.value = AlertDialogState(
            title = getString(context,R.string.check_profile),
            cancelText = getString(context,R.string.cancel),
            confirmText = getString(context,R.string.confirm),
            onClickCancel = {
                resetDialogState()
            },
            onClickConfirm = {
                viewModelScope.launch {

                    var requestImage: MultipartBody.Part? = null
                    val requestNickname = nickname.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    val requestGender = gender.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    profileBmap.value?.let { requestImage = getMultipartBody(getImageUri(context,it),context) }
                    createProfileUseCases.createProfile(requestImage,requestNickname,birthYear.value!!,requestGender)
                        .collect { result ->
                            handleResource(
                                resource = result,
                                onSuccess = { data ->
                                    setEvent(
                                        UiEvent.navigate(
                                            screen = Screen.HomeScreen
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
        _alertDialogState.value = AlertDialogState()
    }
}