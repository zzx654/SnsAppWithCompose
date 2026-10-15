package com.androiddev.snsappwithcompose.feature.createprofile

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.androiddev.domain.use_case.createprofile.CreateProfileUseCases
import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.base.BaseViewModel
import com.androiddev.snsappwithcompose.common.navigation.component.Screen
import com.androiddev.snsappwithcompose.common.base.UiEvent
import com.androiddev.snsappwithcompose.common.state.AlertDialogState
import com.androiddev.snsappwithcompose.common.state.BottomSheetDialogState
import com.androiddev.snsappwithcompose.common.util.UiText
import com.androiddev.snsappwithcompose.feature.createprofile.event.CreateProfileEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val createProfileUseCases: CreateProfileUseCases,
): BaseViewModel() {
    private val _bottomSheetDialogState =  MutableStateFlow(BottomSheetDialogState<CreateProfileImageOption>())
    val bottomSheetDialogState = _bottomSheetDialogState.asStateFlow()


    private val _uiState = MutableStateFlow(CreateProfileUiState())
    val uiState: StateFlow<CreateProfileUiState> = _uiState.asStateFlow()
    private val _bottomWheelDialogState = MutableStateFlow(BottomWheelState())
    val bottomWheelState: StateFlow<BottomWheelState>
        get() = _bottomWheelDialogState.asStateFlow()
    private val _alertDialogState = MutableStateFlow(AlertDialogState())
    val alertDialogState: StateFlow<AlertDialogState>
        get() = _alertDialogState.asStateFlow()

    private var nicknameCheckJob: Job? = null

    fun setProfileBmap(bitmap:Bitmap?) {
        _uiState.update { it.copy(profileBitmap = bitmap) }
    }
    fun onEvent(event: CreateProfileEvent) {
        when (event) {
            is CreateProfileEvent.TypeNickname -> handleNicknameInput(event.nickname)
            is CreateProfileEvent.SetGender -> {
                _uiState.update { it.copy(gender = event.gender) }
                validateForm()
            }
            is CreateProfileEvent.SetBirthYear -> {
                _uiState.update { it.copy(birthYear = event.birthYear) }
                resetBottomWheelDialogState()
                validateForm()
            }
            is CreateProfileEvent.ShowProfileImageOptions -> showBottomSheetDialog()
            is CreateProfileEvent.ShowBirthYearOptions -> showBottomWheelDialog()
            is CreateProfileEvent.ShowCreateProfileAlert -> showCreateProfileAlert(event.imageUri)
        }
    }

    private fun handleNicknameInput(input: String) {
        _uiState.update {
            it.copy(
                nickname = input,
                isTyping = true,
                isNicknameValid = false
            )
        }
        validateForm()

        // 이전 닉네임 검증 타이머 Job 취소 (Debounce 처리)
        nicknameCheckJob?.cancel()

        if (input.length < 2) {
            _uiState.update { it.copy(isTyping = false, isNicknameChecking = false) }
            return
        }

        nicknameCheckJob = viewModelScope.launch {
            delay(500L) // 0.5초 동안 입력이 없으면 중복검사 API 호출
            _uiState.update { it.copy(isTyping = false, isNicknameChecking = true) }

            createProfileUseCases.checkNickname(input).collect { result ->
                result.handle(
                    onSuccess = {
                        val isValid = result.data?.isValid ?: false
                        _uiState.update {
                            it.copy(isNicknameValid = isValid)
                        }
                        validateForm()
                    },
                    onError = {
                        _uiState.update { it.copy(isNicknameValid = false) }
                        validateForm()
                    },
                    onLoading = {
                        _uiState.update { it.copy(isNicknameChecking = true) }
                    },
                    onFinally = {
                        _uiState.update { it.copy(isNicknameChecking = false) }
                    }
                )
            }
        }
    }

    private fun validateForm() {
        val state = _uiState.value
        val isValid = state.isNicknameValid &&
                state.birthYear != null &&
                state.gender.isNotBlank()

        _uiState.update { it.copy(isFormValid = isValid) }
    }
    private fun showBottomSheetDialog() {
        val options = if(_uiState.value.profileBitmap != null) {
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
        resetBottomSheetDialogState()
        viewModelScope.launch {
            when (option) {
                CreateProfileImageOption.Camera -> setEvent(UiEvent.LaunchCamera)
                CreateProfileImageOption.Gallery -> setEvent(UiEvent.LaunchGallery)
                CreateProfileImageOption.Delete -> setProfileBmap(null)
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
        val state = _uiState.value
        _alertDialogState.value = AlertDialogState(
            title = UiText.StringResource(R.string.check_profile),
            cancelText = UiText.StringResource(R.string.cancel),
            confirmText = UiText.StringResource(R.string.confirm),
            onClickCancel = {
                resetDialogState()
            },
            onClickConfirm = {
                viewModelScope.launch {
                    createProfileUseCases.createProfile(
                        profileImage = imageUri,
                        nickname = state.nickname,
                        birth = state.birthYear ?: 2005,
                        gender = state.gender
                    ).collect { result ->
                        result.handle(
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
        _alertDialogState.value = AlertDialogState()
    }
}