package com.androiddev.domain.use_case

data class PostDetailUseCases(
    val ToggleLikePost: ToggleLikePost,
    val GetComments: GetComments,
    val PostComment: PostComment
)