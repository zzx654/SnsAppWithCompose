package com.androiddev.domain.repository

import com.androiddev.domain.model.UploadImageResponse
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface CreateProfileRepository {
    suspend fun uploadImage(requestBody: MultipartBody.Part) : Flow<Resource<UploadImageResponse>>
}