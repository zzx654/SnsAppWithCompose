package com.androiddev.data.repository.createprofile

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.createprofile.CreateProfileApi
import com.androiddev.data.remote.dto.toValidationResult
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.ValidationResult
import com.androiddev.domain.repository.createprofile.CreateProfileRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CreateProfileRepositoryImpl @Inject constructor(
    private val api: CreateProfileApi,
    private val context: Context
) : CreateProfileRepository {
    override suspend fun createProfile(
        profileImage: MultipartBody.Part?,
        nickname: RequestBody,
        birth: Int,
        gender: RequestBody
    ): Flow<Resource<Unit>> = safeApiCall(
        context = context,
        apiCall = { api.createProfile(profileImage,nickname,birth,gender) },
        mapToResource = {}
    )

    override suspend fun checkNickname(nickname: String): Flow<Resource<ValidationResult>> =
        safeApiCall(
            context = context,
            apiCall = { api.checkNickname(nickname) },
            mapToResource = { it.toValidationResult(isValid = it.isValid)}
        )


}