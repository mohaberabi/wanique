package com.mohaberabi.run.presentation.overview.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohaberabi.core.domain.run.repository.RunRepository
import com.mohaberabi.core.domain.session.SessionStorage
import com.mohaberabi.core.domain.sync.RunSyncer
import com.mohaberabi.run.presentation.overview.mapper.toRunModelUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val runSyncer: RunSyncer,
    private val applicationScope: CoroutineScope,
    private val sessionStorage: SessionStorage,
) : ViewModel() {


    var state by mutableStateOf(RunOverviewState())
        private set

    init {
        runRepository.getRuns().onEach { runs ->
            val runsUi = runs.map { it.toRunModelUi() }
            state = state.copy(runs = runsUi)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            runRepository.fetchRuns()
            runRepository.syncPendingRuns()
        }


        viewModelScope.launch {
            runSyncer.scheduleSync(type = RunSyncer.SyncType.FetchRuns(30.minutes))
        }
    }

    fun onAction(action: RunOverviewAction) {
        when (action) {
            is RunOverviewAction.OnStartClick -> {}
            is RunOverviewAction.OnDeleteRun -> {
                viewModelScope.launch {
                    runRepository.deleteRun(id = action.runId)
                }
            }

            is RunOverviewAction.OnAnalyticsClick -> {}
            is RunOverviewAction.OnLogoutClick -> logout()
        }
    }

    private fun logout() {
        applicationScope.launch {
            runSyncer.cancelAllSyncs()
            runRepository.deleteAllRuns()
            runRepository.logout()
            sessionStorage.set(null)
        }
    }
}
