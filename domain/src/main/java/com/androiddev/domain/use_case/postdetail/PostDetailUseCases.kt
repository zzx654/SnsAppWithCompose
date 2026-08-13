package com.androiddev.domain.use_case.postdetail

import javax.inject.Inject

data class PostDetailUseCases @Inject constructor(
    val ToggleLikePost: ToggleLikePost,
    val DeletePost: DeletePost,
    val GetPost:GetPost
)