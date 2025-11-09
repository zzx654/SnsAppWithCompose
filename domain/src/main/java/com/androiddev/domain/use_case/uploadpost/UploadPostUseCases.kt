package com.androiddev.domain.use_case.uploadpost

import com.androiddev.domain.use_case.tag.SearchTag

data class UploadPostUseCases(
    val searchTag: SearchTag,
    val uploadPost: UploadPost,
    val editPost: EditPost
)