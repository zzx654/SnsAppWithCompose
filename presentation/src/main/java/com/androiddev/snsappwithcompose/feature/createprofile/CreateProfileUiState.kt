package com.androiddev.snsappwithcompose.feature.createprofile

import android.graphics.Bitmap

data class CreateProfileUiState(
    val profileBitmap: Bitmap? = null,
    val nickname: String = "",
    val isNicknameValid: Boolean = false,
    val isNicknameChecking: Boolean = false,
    val isTyping: Boolean = false,
    val birthYear: Int? = null,
    val gender: String = "",
    val isFormValid: Boolean = false //버튼 활성화 조건
)