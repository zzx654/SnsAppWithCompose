package com.androiddev.domain.use_case.reply

import javax.inject.Inject

data class ReplyUseCases @Inject constructor(
    val GetSelectedComment: GetSelectedCommentUseCase,
    val GetReplies: GetRepliesUseCase,
    val PostReply: PostReplyUseCase
)