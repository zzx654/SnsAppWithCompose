package com.androiddev.domain.use_case

import com.androiddev.domain.repository.UploadPostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class UploadPost @Inject constructor(
    private val repository: UploadPostRepository
) {
    suspend operator fun invoke(
        anonymousNick: RequestBody?,
        tags: RequestBody?,
        images:List<MultipartBody.Part>?,
        text: RequestBody,
        latitude: MultipartBody.Part? = null,
        longitude: MultipartBody.Part? = null
    ): Flow<Resource<Unit>> = repository.uploadPost(anonymousNick,tags,images,text,latitude,longitude)

}