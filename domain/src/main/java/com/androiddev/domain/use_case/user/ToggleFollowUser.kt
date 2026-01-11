package com.androiddev.domain.use_case.user

import com.androiddev.domain.model.ToggleFollowResult
import com.androiddev.domain.repository.user.UserRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleFollowUser @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        userId:Int
    ): Flow<Resource<ToggleFollowResult>> = repository.toggleFollowUser(userId = userId)
}