package com.mohaberabi.auth.domain.validators

interface PatternValidator {


    fun matches(value: String): Boolean

}