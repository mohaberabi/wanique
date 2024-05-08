package com.mohaberabi.data.validators

import android.util.Patterns
import com.mohaberabi.auth.domain.validators.PatternValidator

object EmailPatternValidator : PatternValidator {

    override fun matches(value: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }
}