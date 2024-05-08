package com.mohaberabi.wear.run.presentation.tracker.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.content.ContextCompat


fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED

fun Context.hasNotificationsPermissions(): Boolean {

    return if (!requiresNotificaitonPermisison()) {
        true
    } else {
        hasPermission(Manifest.permission.POST_NOTIFICATIONS)
    }
}

fun Context.hasBodyPermissions(): Boolean = hasPermission(Manifest.permission.BODY_SENSORS)


@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun requiresNotificaitonPermisison(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU