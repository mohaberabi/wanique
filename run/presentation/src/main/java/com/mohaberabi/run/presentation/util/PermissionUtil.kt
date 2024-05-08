package com.mohaberabi.run.presentation.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.content.ContextCompat


@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun requiresNotificationPermission(): Boolean = Build.VERSION.SDK_INT >= 33
fun ComponentActivity.shouldShowLocationPermissionRationale(): Boolean =
    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)

fun ComponentActivity.shouldShowNotificationRationale(): Boolean =
    requiresNotificationPermission() && shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)

fun Context.hasPermission(permission: String): Boolean = ContextCompat.checkSelfPermission(
    this,
    permission
) == PackageManager.PERMISSION_GRANTED

fun Context.hasLocationPermission(): Boolean =
    hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

fun Context.hasNotificationPermission(): Boolean =
    if (requiresNotificationPermission()) hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) else true
