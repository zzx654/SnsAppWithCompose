package com.androiddev.data.repository.uploadpost

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.androiddev.data.R
import com.androiddev.data.remote.api.uploadpost.UploadPostApi
import com.androiddev.data.remote.dto.toGetPostsResponse
import com.androiddev.data.remote.dto.toPosts
import com.androiddev.data.util.safeApiCall
import com.androiddev.domain.model.Posts
import com.androiddev.domain.repository.uploadpost.UploadPostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UploadPostRepositoryImpl @Inject constructor(
    private val api: UploadPostApi,
    private val context: Context
): UploadPostRepository {


    override suspend fun uploadPost(
        anonymousNick: RequestBody?,
        tags: RequestBody?,
        images: List<MultipartBody.Part>?,
        audio:MultipartBody.Part?,
        voteOptions:RequestBody?,
        text: RequestBody,
        latitude: MultipartBody.Part?,
        longitude: MultipartBody.Part?
    ): Flow<Resource<Unit>> = safeApiCall(
        context = context,
        apiCall = { api.uploadPost(anonymousNick,tags,images,audio,voteOptions,text,latitude,longitude) },
        mapToResource = {}
    )

    override suspend fun editPost(
        postid: MultipartBody.Part,
        latitude: MultipartBody.Part?,
        longitude: MultipartBody.Part?,
        anonymousNick: RequestBody?,
        deleteImages: RequestBody?,
        tags: RequestBody?,
        image: List<MultipartBody.Part>?,
        audio: MultipartBody.Part?,
        deleteAudio: RequestBody?,
        text: RequestBody
    ): Flow<Resource<Posts>> = safeApiCall(
        context = context,
        apiCall = { api.editPost(postid,latitude,longitude,anonymousNick,deleteImages,tags,image,audio,deleteAudio,text) },
        mapToResource = { it.toPosts(posts = it.posts) }
    )

}
