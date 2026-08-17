package com.androiddev.domain.use_case.comment

import androidx.paging.PagingData
import com.androiddev.domain.model.Comment
import com.androiddev.domain.model.CommentSortType
import com.androiddev.domain.repository.postdetail.CommentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val repository: CommentRepository
) {
    suspend operator fun invoke(postId: Int, sortType: CommentSortType): Flow<PagingData<Comment>> {
        return repository.getComments(postId, sortType)
    }
}