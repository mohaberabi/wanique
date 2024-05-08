package com.mohaberabi.core.domain.sync

import com.mohaberabi.core.domain.model.RunModel
import com.mohaberabi.core.domain.run.source.RunId
import kotlin.time.Duration

interface RunSyncer {


    suspend fun scheduleSync(type: SyncType)
    suspend fun cancelAllSyncs()
    sealed interface SyncType {

        data class FetchRuns(val interval: Duration) : SyncType

        data class DeleteRun(val runId: RunId) : SyncType

        class CreateRun(val run: RunModel, val mapPictureBytes: ByteArray) : SyncType
    }
}