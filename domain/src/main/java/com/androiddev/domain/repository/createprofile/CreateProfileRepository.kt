package com.androiddev.domain.repository.createprofile

import android.net.Uri
import com.androiddev.domain.model.ValidationResult
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface CreateProfileRepository {
    suspend fun createProfile(
        profileImage: Uri?,
        nickname: String,
        birth: Int,
        gender: String
    ) : Flow<Resource<Unit>>
    suspend fun checkNickname(nickname: String): Flow<Resource<ValidationResult>>
}