package com.androiddev.domain.use_case

import com.androiddev.domain.repository.CreateProfileRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckNickname @Inject constructor(
    private val repository: CreateProfileRepository
) {
    suspend operator fun invoke(nickname: String):Flow<Resource<Boolean>> = repository.checkNickname(nickname)
}