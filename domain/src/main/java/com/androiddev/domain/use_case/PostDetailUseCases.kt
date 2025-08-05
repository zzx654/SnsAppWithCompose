package com.androiddev.domain.use_case

data class PostDetailUseCases(
    val ToggleLikePost: ToggleLikePost,
    val GetComments: GetComments,
    val GetPopularComments: GetPopularComments,
    val PostComment: PostComment,
    val ToggleLikeComment: ToggleLikeComment
)