package com.androiddev.domain.use_case.postdetail

import com.androiddev.domain.model.NotificationComment
import com.androiddev.domain.repository.postdetail.CommentRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationComment @Inject constructor(
    private val repository: CommentRepository
) {
    suspend operator fun invoke(commentId:Int): Flow<Resource<NotificationComment>> = repository.getNotificationComment(commentId)
}