package com.androiddev.domain.use_case

import com.androiddev.domain.model.GetPostsResponse
import com.androiddev.domain.repository.UploadPostRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class EditPost @Inject constructor(
    private val repository: UploadPostRepository
) {
    suspend operator fun invoke(
        postid: MultipartBody.Part,
        latitude: MultipartBody.Part?,
        longitude: MultipartBody.Part?,
        anonymousNick: RequestBody?,
        deleteImages: RequestBody?,
        tags: RequestBody?,
        images:List<MultipartBody.Part>?,
        audio:MultipartBody.Part?,
        deleteAudio:RequestBody?,
        text: RequestBody
    ): Flow<Resource<GetPostsResponse>> = repository.editPost(postid,latitude,longitude,anonymousNick,deleteImages,tags,images,audio,deleteAudio,text)

}