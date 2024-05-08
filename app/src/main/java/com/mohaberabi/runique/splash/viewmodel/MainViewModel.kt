package com.mohaberabi.runique.splash.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohaberabi.core.domain.session.SessionStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class MainViewModel(
    private val sessionStorage: SessionStorage,
) : ViewModel() {


    var state by mutableStateOf(MainState())
        private set

    init {

        viewModelScope.launch {
            state = state.copy(
                isChecking = true
            )
            state = state.copy(
                isLoggedIn = sessionStorage.get() != null
            )
            state = state.copy(
                isChecking = false
            )
        }
    }

    fun toggleAnalyticsDownloaderDialog(visible: Boolean) {
        state = state.copy(showAnalyticsInstallerDialog = visible)
    }
}