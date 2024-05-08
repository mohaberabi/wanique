package com.mohaberabi.run.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mohaberabi.core.domain.model.AppAltitude
import com.mohaberabi.run.domain.LocationObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidLocationObserver(
    private val context: Context,

    ) : LocationObserver {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    override fun observeLocation(
        interval:
        Long
    ): Flow<AppAltitude> {

        return callbackFlow {
            val locationManager = context.getSystemService<LocationManager>()!!
            var isGpsLocationProviderEnabled = false
            var isNetworkLocationProviderEnabled = false

            while (!isGpsLocationProviderEnabled && !isNetworkLocationProviderEnabled) {
                isGpsLocationProviderEnabled =
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                isNetworkLocationProviderEnabled =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                if (!isGpsLocationProviderEnabled && !isNetworkLocationProviderEnabled) {
                    delay(3000L)
                }

            }
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                close()
                return@callbackFlow
            } else {
                client.lastLocation.addOnSuccessListener {
                    it?.let { location ->
                        trySend(location.toAppAltitude())
                    }
                }

                val request = com.google.android.gms.location.LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    interval
                ).build()

                val locationCallback = object : LocationCallback() {

                    override fun onLocationResult(result: LocationResult) {
                        super.onLocationResult(result)
                        result.locations.lastOrNull()?.let { location ->
                            trySend(location.toAppAltitude())
                        }
                    }
                }
                client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
                awaitClose {
                    client.removeLocationUpdates(locationCallback)
                }
            }

        }
    }
}