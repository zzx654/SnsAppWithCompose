package com.androiddev.domain.use_case.reply

import androidx.paging.PagingData
import com.androiddev.domain.model.Comment
import com.androiddev.domain.repository.postdetail.CommentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRepliesUseCase @Inject constructor(
    private val repository: CommentRepository
) {
    suspend operator fun invoke(ref:Int): Flow<PagingData<Comment>> {
        return repository.getReplies(ref)
    }
}