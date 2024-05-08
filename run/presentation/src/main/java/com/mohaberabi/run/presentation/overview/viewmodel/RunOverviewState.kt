package com.mohaberabi.run.presentation.overview.viewmodel

import com.mohaberabi.run.presentation.overview.util.RunModelUi

data class RunOverviewState(
    val runs: List<RunModelUi> = emptyList(),
)
