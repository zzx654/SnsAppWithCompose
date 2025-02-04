package com.androiddev.domain.use_case

import android.graphics.Bitmap
import com.androiddev.domain.model.UploadImageResponse
import com.androiddev.domain.repository.CreateProfileRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class UploadImage @Inject constructor(
    private val repository: CreateProfileRepository
) {
    suspend operator fun invoke(
        profileImage: MultipartBody.Part?,
        nickname: RequestBody,
        birth: Int,
        gender: RequestBody
    ):Flow<Resource<UploadImageResponse>> = repository.uploadImage(profileImage,nickname,birth,gender)
}