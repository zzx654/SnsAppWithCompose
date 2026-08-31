package com.androiddev.domain.use_case.uploadpost

import com.androiddev.domain.model.Post
import com.androiddev.domain.repository.uploadpost.UploadPostRepository
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
        deletedVisualMedia: RequestBody?,
        tags: RequestBody?,
        media:List<MultipartBody.Part>?,
        mediaTypes:List<RequestBody>?,
        deletedAudio:RequestBody?,
        text: RequestBody
    ): Flow<Resource<List<Post>>> = repository.editPost(
        postid = postid,
        latitude = latitude,
        longitude = longitude,
        anonymousNick = anonymousNick,
        deletedVisualMedia = deletedVisualMedia,
        tags = tags,
        media = media,
        mediaTypes = mediaTypes,
        deletedAudio = deletedAudio,
        text = text
    )

}