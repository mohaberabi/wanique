package com.mohaberabi.core.presentation.ui.util

import com.mohaberabi.core.domain.utils.error.DataError
import com.mohaberabi.core.presentation.ui.R


fun DataError.asUiText(): UiText {

    return when (this) {
        DataError.Local.DISK_FULL -> UiText.StringResource(R.string.disk_full)
        DataError.Network.NO_NETWORK -> UiText.StringResource(R.string.no_netowrk)
        DataError.Network.CONFLICT -> UiText.StringResource(R.string.conflict)
        DataError.Network.SERIALIZATION_ERROR -> UiText.StringResource(R.string.serialize_error)
        DataError.Network.UNKNOWN_ERROR -> UiText.StringResource(R.string.unknown_error)
        DataError.Network.PAYLOAD_TOO_LARGE -> UiText.StringResource(R.string.payload_too_large)
        DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(R.string.request_timeout)
        DataError.Network.TOO_MANY_REQUEST -> UiText.StringResource(R.string.too_many_request)
        DataError.Network.SERVER_ERROR -> UiText.StringResource(R.string.server_error)
        DataError.Network.UNAUTHORIZED -> UiText.StringResource(R.string.unAuthed)

        else -> UiText.StringResource(R.string.unknown_error)
    }
}