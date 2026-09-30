package com.androiddev.domain.use_case.user

import androidx.paging.PagingData
import com.androiddev.domain.model.User
import com.androiddev.domain.model.Users
import com.androiddev.domain.repository.user.UserRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchedUsers @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(
        nickname:String
    ): Flow<PagingData<User>> = repository.getSearchedUsers(nickname)
}
