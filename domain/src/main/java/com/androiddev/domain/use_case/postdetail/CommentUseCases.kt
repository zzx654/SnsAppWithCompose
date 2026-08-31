package com.androiddev.domain.use_case.postdetail

import com.androiddev.domain.use_case.comment.GetCommentsUseCase
import javax.inject.Inject

data class CommentUseCases @Inject constructor(
    val GetComments: GetCommentsUseCase,
    val GetPopularComments: GetPopularComments,
    val ToggleLikeComment: ToggleLikeComment,
    val PostComment: PostComment,
    val GetNotificationComment: GetNotificationComment
)