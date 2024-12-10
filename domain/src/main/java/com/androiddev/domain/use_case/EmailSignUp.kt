package com.androiddev.domain.use_case

import com.androiddev.domain.repository.SignupRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EmailSignUp @Inject constructor(
    private val repository: SignupRepository
) {
    suspend operator fun invoke(account:String,password:String,phonenumber:String,authCode:String): Flow<Resource<Boolean>> = repository.emailSignUp(account, password, phonenumber, authCode)
}