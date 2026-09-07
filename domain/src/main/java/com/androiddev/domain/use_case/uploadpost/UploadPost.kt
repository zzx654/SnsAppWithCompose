package com.androiddev.domain.use_case.uploadpost

import com.androiddev.domain.model.UploadPostParam
import com.androiddev.domain.repository.uploadpost.UploadPostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadPost @Inject constructor(
    private val repository: UploadPostRepository
) {
    suspend operator fun invoke(
        param:UploadPostParam
    ): Flow<Resource<Unit>> {
        return repository.uploadPost(
            param = param,
        )

    }

}