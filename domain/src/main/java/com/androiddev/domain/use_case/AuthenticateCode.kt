package com.androiddev.domain.use_case

import com.androiddev.domain.repository.AuthPhoneRepository
import javax.inject.Inject

class AuthenticateCode @Inject constructor(
    private val repository: AuthPhoneRepository
) {
    suspend operator fun invoke(phoneNumber: String,authCode:String) = repository.authenticateCode(phoneNumber,authCode)
}