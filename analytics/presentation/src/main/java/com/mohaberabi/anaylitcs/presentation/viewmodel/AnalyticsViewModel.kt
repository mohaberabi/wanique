package com.mohaberabi.anaylitcs.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohaberabi.anayltics.domain.AnalyticsRepository
import kotlinx.coroutines.launch

class AnalyticsViewModel(
    private val analyticsRepository: AnalyticsRepository,
) : ViewModel() {


    var state by mutableStateOf(AnalyticsState())
        private set


    init {
        viewModelScope.launch {
            state = analyticsRepository.getAnalytics().toAnalyticsState()
        }
    }
}