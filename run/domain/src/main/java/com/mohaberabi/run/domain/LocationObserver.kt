package com.mohaberabi.run.domain

import com.mohaberabi.core.domain.model.AppAltitude
import kotlinx.coroutines.flow.Flow


interface LocationObserver {

    fun observeLocation(interval: Long): Flow<AppAltitude>
}