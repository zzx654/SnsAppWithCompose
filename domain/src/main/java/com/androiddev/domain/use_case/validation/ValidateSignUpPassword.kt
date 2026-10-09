package com.androiddev.domain.use_case.validation

import java.util.regex.Pattern
import javax.inject.Inject

class ValidateSignUpPassword @Inject constructor() {
    private val passwordPattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$"


    operator fun invoke(password: String): Boolean {
        return Pattern.matches(passwordPattern,password)
    }
}