package com.mohaberabi.run.presentation.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.maps.android.compose.Polyline
import com.mohaberabi.core.domain.model.LocationTimestamp


@Composable
fun RuniquePolyLine(


    locations: List<List<LocationTimestamp>>,
) {
    val polylines = remember(locations) {

        locations.map { list ->
            list.zipWithNext { locTime1, locTime2 ->
                PolyLineUi(
                    location1 = locTime1.location.latLng,
                    location2 = locTime2.location.latLng,
                    color = PolyLineColorCalc.locationsToColor(
                        location1 = locTime1,
                        location2 = locTime2
                    )
                )
            }
        }
    }

    polylines.forEach { polyline ->
        polyline.forEach { ui ->
            Polyline(
                color = ui.color,
                jointType = JointType.BEVEL,
                points = listOf(

                    LatLng(
                        ui.location1.lat,
                        ui.location1.lng
                    ),
                    LatLng(
                        ui.location2.lat,
                        ui.location2.lng
                    )
                )
            )
        }
    }

}