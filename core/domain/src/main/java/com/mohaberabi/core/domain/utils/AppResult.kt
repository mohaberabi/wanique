package com.mohaberabi.core.domain.utils

import com.mohaberabi.core.domain.utils.error.AppError


sealed interface AppResult<out D, out E : AppError> {
    data class Done<out D>(val data: D) : AppResult<D, Nothing>
    data class Error<out E : AppError>(val error: E) : AppResult<Nothing, E>

}


inline fun <T, E : AppError, R> AppResult<T, E>.map(map: (T) -> R): AppResult<R, E> {
    return when (this) {
        is AppResult.Error -> AppResult.Error(error)
        is AppResult.Done -> AppResult.Done(map(data))
    }
}

fun <T, E : AppError> AppResult<T, E>.asEmptyResult(): EmptyDataResult<E> {
    return map { }
}
typealias EmptyDataResult<E> = AppResult<Unit, E>