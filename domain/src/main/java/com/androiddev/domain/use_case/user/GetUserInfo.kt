package com.androiddev.domain.use_case.user

import com.androiddev.domain.model.User
import com.androiddev.domain.model.Users
import com.androiddev.domain.repository.user.UserRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserInfo @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        userId:Int
    ): Flow<Resource<Users>> = repository.getUserInfo(userId)
}