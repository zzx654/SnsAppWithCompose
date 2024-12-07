package com.androiddev.domain.use_case

import android.content.res.Resources
import com.androiddev.domain.R
import com.androiddev.domain.repository.AuthPhoneRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import java.util.regex.Pattern
import javax.inject.Inject

class RequestPhoneAuthCode @Inject constructor(
    private val repository: AuthPhoneRepository
) {
    @kotlin.jvm.Throws(InvalidPhoneNumberException::class)
    suspend operator fun invoke(PhoneNumber: String): Flow<Resource<Unit>> {
        if(PhoneNumber.isBlank())
            throw InvalidPhoneNumberException(Resources.getSystem().getString(R.string.empty_phone))
        else if(!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",PhoneNumber))
            throw InvalidPhoneNumberException(Resources.getSystem().getString(R.string.invalid_phone))
        return repository.requestAuthCode(PhoneNumber)
    }
}
class InvalidPhoneNumberException(message: String): Exception(message)