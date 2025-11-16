package com.androiddev.domain.repository.createprofile

import com.androiddev.domain.model.ValidationResult
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface CreateProfileRepository {
    suspend fun createProfile(
        profileImage: MultipartBody.Part?,
        nickname: RequestBody,
        birth: Int,
        gender: RequestBody
    ) : Flow<Resource<Unit>>
    suspend fun checkNickname(nickname: String): Flow<Resource<ValidationResult>>
}