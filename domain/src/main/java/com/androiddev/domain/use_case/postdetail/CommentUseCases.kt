package com.androiddev.domain.use_case.postdetail

data class CommentUseCases(
    val GetSelectedComment: GetSelectedComment,
    val GetComments: GetComments,
    val GetPopularComments: GetPopularComments,
    val ToggleLikeComment: ToggleLikeComment,
    val PostComment: PostComment
)