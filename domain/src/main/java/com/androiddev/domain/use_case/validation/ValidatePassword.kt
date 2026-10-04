package com.androiddev.domain.use_case.validation

import javax.inject.Inject

class ValidatePassword @Inject constructor() {
    operator fun invoke(password: String): Boolean {
        return password.isNotBlank()
    }
}