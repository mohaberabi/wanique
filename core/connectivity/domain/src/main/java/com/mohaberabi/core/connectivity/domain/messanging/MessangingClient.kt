package com.mohaberabi.core.connectivity.domain.messanging

import com.mohaberabi.core.domain.utils.EmptyDataResult
import kotlinx.coroutines.flow.Flow

interface MessangingClient {


    fun connectNode(nodeId: String): Flow<MessageAction>

    suspend fun sendOrQueueAction(action: MessageAction): EmptyDataResult<MessaningError>
}