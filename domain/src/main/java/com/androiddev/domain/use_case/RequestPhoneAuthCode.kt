package com.androiddev.domain.use_case

import android.content.Context
import android.content.res.Resources
import androidx.core.content.ContextCompat.getString
import com.androiddev.domain.Constants.PHONE_REGEX
import com.androiddev.domain.R
import com.androiddev.domain.repository.AuthPhoneRepository
import com.androiddev.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import java.util.regex.Pattern
import javax.inject.Inject

class RequestPhoneAuthCode @Inject constructor(
    private val repository: AuthPhoneRepository,
    private val context: Context
) {
    @kotlin.jvm.Throws(InvalidPhoneNumberException::class)
    suspend operator fun invoke(PhoneNumber: String): Flow<Resource<Boolean>> {
        if(PhoneNumber.isBlank())
            throw InvalidPhoneNumberException(getString(context,R.string.empty_phone))
        else if(!Pattern.matches(PHONE_REGEX,PhoneNumber))
            throw InvalidPhoneNumberException(getString(context,R.string.invalid_phone))
        return repository.requestAuthCode(PhoneNumber)
    }
}
class InvalidPhoneNumberException(message: String): Exception(message)