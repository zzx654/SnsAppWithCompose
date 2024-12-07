package com.androiddev.domain.use_case

import android.content.res.Resources
import com.androiddev.domain.R
import com.androiddev.domain.repository.SignupRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RequestEmailAuthCode @Inject constructor(
    private val repository: SignupRepository
) {
    @kotlin.jvm.Throws(InvalidPhoneNumberException::class)
    suspend operator fun invoke(email: String): Flow<Resource<Unit>> {
        if(email.isBlank())
            throw InvalidPhoneNumberException(Resources.getSystem().getString(R.string.empty_email))
        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() )
            throw InvalidPhoneNumberException(Resources.getSystem().getString(R.string.invalid_email))
        return repository.requestAuthCode(email)
    }
}
class InvalidEmailException(message: String): Exception(message)