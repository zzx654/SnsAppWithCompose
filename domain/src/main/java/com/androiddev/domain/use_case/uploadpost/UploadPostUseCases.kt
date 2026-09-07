package com.androiddev.domain.use_case.uploadpost

import com.androiddev.domain.use_case.tag.SearchTag
import javax.inject.Inject

data class UploadPostUseCases @Inject constructor(
    val searchTag: SearchTag,
    val uploadPost: UploadPost,
    val editPost: EditPost
)