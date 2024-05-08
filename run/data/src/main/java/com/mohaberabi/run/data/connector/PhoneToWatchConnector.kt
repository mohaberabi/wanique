package com.mohaberabi.run.data.connector

import com.mohaberabi.core.connectivity.domain.DeviceNode
import com.mohaberabi.core.connectivity.domain.DeviceType
import com.mohaberabi.core.connectivity.domain.NodeDiscovery
import com.mohaberabi.core.connectivity.domain.messanging.MessageAction
import com.mohaberabi.core.connectivity.domain.messanging.MessangingClient
import com.mohaberabi.core.connectivity.domain.messanging.MessaningError
import com.mohaberabi.core.domain.utils.EmptyDataResult
import com.mohaberabi.run.domain.WatchConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class PhoneToWatchConnector(
    nodeDiscovery: NodeDiscovery,
    applicationScope: CoroutineScope,
    private val messagingClient: MessangingClient,
) : WatchConnector {


    private val _connectedDevice = MutableStateFlow<DeviceNode?>(null)


    override val connectedDevice: StateFlow<DeviceNode?> = _connectedDevice.asStateFlow()


    /**
     * think of this as a bridge between the phone and the wearable
     * now imagine if the phone is connected to the wearable
     * then also imagine  if you have a dialog in the phone which you need to dismiss from the wearable
     * such a behaviour could be done using  [messagingActions]
     * it is the way that can both devices
     */
    override val messagingActions: Flow<MessageAction> =
        nodeDiscovery
            .observeConnectedDevice(DeviceType.PHONE)
            .flatMapLatest { connected ->
                val node = connected.firstOrNull()
                if (node != null && node.isNearby) {
                    _connectedDevice.update { node }
                    messagingClient.connectNode(node.id)
                } else flowOf()

            }.onEach { action ->
                if (action == MessageAction.ConnectionRequest) {
                    if (isTrackable.value) {
                        sendActionToWatch(MessageAction.Trackable)
                    } else {
                        sendActionToWatch(MessageAction.UnTrackable)
                    }
                }
                /**
                 * [shareIn] a single stream to listen to it from any place of the app to prevent creating a new instance each time
                 * with a new values of a new stream of required data
                 */
            }.shareIn(
                applicationScope,
                /**
                 * [Eagerly] starts immediate
                 */
                SharingStarted.Eagerly,
            )

    override suspend fun sendActionToWatch(
        action:
        MessageAction
    ): EmptyDataResult<MessaningError> {
        return messagingClient.sendOrQueueAction(action)
    }


    private val isTrackable = MutableStateFlow(false)
    override fun setIsTrackable(isTrackable: Boolean) {
        this.isTrackable.update { isTrackable }
    }

    init {

        _connectedDevice
            .filterNotNull()
            .flatMapLatest { isTrackable }
            .onEach { canTrack ->
                sendActionToWatch(MessageAction.ConnectionRequest)
                val action = if (canTrack) {
                    MessageAction.Trackable
                } else MessageAction.UnTrackable
                sendActionToWatch(action)

            }.launchIn(applicationScope)

    }
}