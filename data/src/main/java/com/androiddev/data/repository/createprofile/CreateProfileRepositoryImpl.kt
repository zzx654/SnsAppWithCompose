package com.androiddev.data.repository.createprofile

import android.content.Context
import android.net.Uri
import com.androiddev.data.remote.api.createprofile.CreateProfileApi
import com.androiddev.data.remote.dto.toDomain
import com.androiddev.data.util.getMultipartBody
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.MediaType
import com.androiddev.domain.model.ValidationResult
import com.androiddev.domain.repository.createprofile.CreateProfileRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class CreateProfileRepositoryImpl @Inject constructor(
    private val api: CreateProfileApi,
    private val context: Context
) : CreateProfileRepository {
    override suspend fun createProfile(
        profileImage: Uri?,
        nickname: String,
        birth: Int,
        gender: String
    ): Flow<Resource<Unit>> = safeApiCall(
        context = context,
        apiCall = {

            val nicknameBody = nickname.toRequestBody("text/plain".toMediaTypeOrNull())

            val genderBody = gender.toRequestBody("text/plain".toMediaTypeOrNull())

            val profileImageBody = profileImage?.let {
                getMultipartBody(uri = it,context = context, type = MediaType.IMAGE)
            }


            api.createProfile(profileImageBody,nicknameBody,birth,genderBody)

                  },
        mapToResource = {}
    )

    override suspend fun checkNickname(nickname: String): Flow<Resource<ValidationResult>> =
        safeApiCall(
            context = context,
            apiCall = { api.checkNickname(nickname) },
            mapToResource = { it.toDomain()}
        )


}