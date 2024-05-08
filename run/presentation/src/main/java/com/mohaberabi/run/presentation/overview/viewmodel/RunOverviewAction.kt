package com.mohaberabi.run.presentation.overview.viewmodel


sealed interface RunOverviewAction {
    data object OnStartClick : RunOverviewAction
    data object OnLogoutClick : RunOverviewAction
    data object OnAnalyticsClick : RunOverviewAction
    data class OnDeleteRun(val runId: String) : RunOverviewAction
}