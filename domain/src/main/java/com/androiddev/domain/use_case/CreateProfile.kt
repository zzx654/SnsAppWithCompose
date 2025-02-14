package com.androiddev.domain.use_case

import com.androiddev.domain.repository.CreateProfileRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class CreateProfile @Inject constructor(
    private val repository: CreateProfileRepository
) {
    suspend operator fun invoke(
        profileImage: MultipartBody.Part?,
        nickname: RequestBody,
        birth: Int,
        gender: RequestBody
    ):Flow<Resource<Boolean>> = repository.createProfile(profileImage,nickname,birth,gender)
}