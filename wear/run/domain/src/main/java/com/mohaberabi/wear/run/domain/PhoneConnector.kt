package com.mohaberabi.wear.run.domain

import com.mohaberabi.core.connectivity.domain.DeviceNode
import com.mohaberabi.core.connectivity.domain.messanging.MessageAction
import com.mohaberabi.core.connectivity.domain.messanging.MessaningError
import com.mohaberabi.core.domain.utils.EmptyDataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface PhoneConnector {


    val connectedNode: StateFlow<DeviceNode?>
    val messagingActions: Flow<MessageAction>
    suspend fun sendActionToPhone(action: MessageAction): EmptyDataResult<MessaningError>

}