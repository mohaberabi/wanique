package com.mohaberabi.run.domain

import com.mohaberabi.core.connectivity.domain.DeviceNode
import com.mohaberabi.core.connectivity.domain.messanging.MessageAction
import com.mohaberabi.core.connectivity.domain.messanging.MessaningError
import com.mohaberabi.core.domain.utils.EmptyDataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WatchConnector {
    /**
     * [WatchConnector] is the interface that will allow the phone to connect to the [Wearable] Device
     * and in order to do such we need to listen for continues Connection of the device [Wearable] we
     * are now connected to , also we might loss connection so that's why it might be [null]
     */
    val connectedDevice: StateFlow<DeviceNode?>


    /**
     * the use might not allow permissions to connect to location so it might not allow to track the run
     *  this[setIsTrackable] is needed FOR such handling
     */
    fun setIsTrackable(isTrackable: Boolean)


    /**
     * the flow which  will be responsible for listening to the other end values to be handed from receiver end
     */
    val messagingActions: Flow<MessageAction>

    /**
     * so you will receive an action , it makes sense to also send some
     */
    suspend fun sendActionToWatch(action: MessageAction): EmptyDataResult<MessaningError>

}