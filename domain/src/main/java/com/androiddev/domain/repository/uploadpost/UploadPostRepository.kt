package com.androiddev.domain.repository.uploadpost

import com.androiddev.domain.location.LocationState
import com.androiddev.domain.model.Post
import com.androiddev.domain.model.UploadPostParam
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UploadPostRepository {


    suspend fun uploadPost(
        param: UploadPostParam
    ): Flow<Resource<Unit>>
    suspend fun editPost(
        postid:Int,
        param: UploadPostParam,
        deletedVisualMedia: List<String>,
        deletedAudio: String?,
    ): Flow<Resource<List<Post>>>

}