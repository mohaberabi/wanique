package com.mohaberabi.runique.splash.viewmodel


data class MainState(
    val isLoggedIn: Boolean = false,
    val isChecking: Boolean = false,
    val showAnalyticsInstallerDialog: Boolean = false
)