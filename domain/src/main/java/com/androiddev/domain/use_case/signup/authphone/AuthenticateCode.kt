package com.androiddev.domain.use_case.signup.authphone

import com.androiddev.domain.repository.signup.AuthPhoneRepository
import javax.inject.Inject

class AuthenticateCode @Inject constructor(
    private val repository: AuthPhoneRepository
) {
    suspend operator fun invoke(phoneNumber: String,authCode:String) = repository.authenticateCode(phoneNumber,authCode)
}