package com.mohaberabi.run.location

import android.location.Location
import com.mohaberabi.core.domain.model.AppAltitude
import com.mohaberabi.core.domain.model.AppLatLng


fun Location.toAppAltitude(): AppAltitude {


    return AppAltitude(
        latLng = AppLatLng(
            lat = latitude,
            lng = longitude,
        ),
        altitude = altitude

    )
}