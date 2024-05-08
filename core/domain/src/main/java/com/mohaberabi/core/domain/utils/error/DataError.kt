package com.mohaberabi.core.domain.utils.error

sealed interface DataError : AppError {


    enum class Network : DataError {

        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        CONFLICT,
        TOO_MANY_REQUEST,
        NO_NETWORK,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERIALIZATION_ERROR,
        UNKNOWN_ERROR

    }


    enum class Local : DataError {
        DISK_FULL,
        UNKNOWN,

    }
}