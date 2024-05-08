package com.mohaberabi.wear.run.data.repository

import android.app.Application
import android.content.Context
import com.mohaberabi.core.connectivity.domain.DeviceNode
import com.mohaberabi.core.connectivity.domain.DeviceType
import com.mohaberabi.core.connectivity.domain.NodeDiscovery
import com.mohaberabi.core.connectivity.domain.messanging.MessageAction
import com.mohaberabi.core.connectivity.domain.messanging.MessangingClient
import com.mohaberabi.core.connectivity.domain.messanging.MessaningError
import com.mohaberabi.core.domain.utils.EmptyDataResult
import com.mohaberabi.wear.run.domain.PhoneConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class WatchToPhoneConnector(
    nodeDiscovery: NodeDiscovery,
    applicationScope: CoroutineScope,
    private val messagingClient: MessangingClient,
) : PhoneConnector {
    private val _connectedNode = MutableStateFlow<DeviceNode?>(null)
    override val connectedNode: StateFlow<DeviceNode?> = _connectedNode.asStateFlow()


    override val messagingActions: Flow<MessageAction> =
        nodeDiscovery.observeConnectedDevice(DeviceType.WATCH)
            .flatMapLatest { connected ->

                val node = connected.firstOrNull()
                if (node != null && node.isNearby) {
                    _connectedNode.update { node }
                    messagingClient.connectNode(node.id)
                } else flowOf()
            }.shareIn(
                applicationScope,

                SharingStarted.Eagerly
            )


    override suspend fun sendActionToPhone(
        action:
        MessageAction
    ): EmptyDataResult<MessaningError> = messagingClient.sendOrQueueAction(action)
}