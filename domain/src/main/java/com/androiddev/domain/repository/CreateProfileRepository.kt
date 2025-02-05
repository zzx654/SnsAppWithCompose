package com.androiddev.domain.repository

import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface CreateProfileRepository {
    suspend fun uploadImage(
        profileImage: MultipartBody.Part?,
        nickname: RequestBody,
        birth: Int,
        gender: RequestBody
    ) : Flow<Resource<Boolean>>
    suspend fun checkNickname(nickname: String): Flow<Resource<Boolean>>
}