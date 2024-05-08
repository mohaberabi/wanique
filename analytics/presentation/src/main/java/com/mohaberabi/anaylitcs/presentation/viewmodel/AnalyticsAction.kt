package com.mohaberabi.anaylitcs.presentation.viewmodel

sealed interface AnalyticsAction {
    data object OnBackClick : AnalyticsAction
}