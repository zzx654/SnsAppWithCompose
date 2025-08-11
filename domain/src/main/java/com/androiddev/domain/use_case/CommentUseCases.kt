package com.androiddev.domain.use_case

data class CommentUseCases(
    val GetSelectedComment:GetSelectedComment,
    val GetComments: GetComments,
    val GetPopularComments: GetPopularComments,
    val ToggleLikeComment: ToggleLikeComment,
    val PostComment: PostComment
)