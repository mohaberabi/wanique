package com.mohaberabi.core.connectivity.domain.messanging

import com.mohaberabi.core.domain.utils.error.AppError

enum class MessaningError : AppError {


    CONNECTION_INTERRUPTED,
    DISCONNECTED,
    UNKNOWN
}