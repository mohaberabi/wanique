package com.mohaberabi.core.connectivity.data.messanging

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.mohaberabi.core.connectivity.data.messanging.WearMessaningClient.Companion.BASE_BASE_MESSAGING_ACTION
import com.mohaberabi.core.connectivity.domain.messanging.MessageAction
import com.mohaberabi.core.connectivity.domain.messanging.MessangingClient
import com.mohaberabi.core.connectivity.domain.messanging.MessaningError
import com.mohaberabi.core.domain.utils.AppResult
import com.mohaberabi.core.domain.utils.EmptyDataResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WearMessaningClient(
    private val context: Context,
) : MessangingClient {


    private val client = Wearable.getMessageClient(context)
    private val messageQueue = mutableListOf<MessageAction>()
    private var connectedNodeId: String? = null

    override fun connectNode(
        nodeId: String
    ): Flow<MessageAction> {
        connectedNodeId = nodeId
        return callbackFlow {
            val listener: (MessageEvent) -> Unit = { event ->
                if (event.path.startsWith(BASE_BASE_MESSAGING_ACTION)) {
                    val json = event.data.decodeToString()
                    val action = Json.decodeFromString<MessageActionDto>(json)
                    trySend(action.toMessageAction())
                }
            }


            client.addListener(listener)
            messageQueue.forEach {
                sendOrQueueAction(it)
            }
            messageQueue.clear()
            awaitClose {
                client.removeListener(listener)
            }
        }
    }

    /**
     * this is the method where we use to send messages to end devices
     * it takes an [action] from the end device which it wants to inform the other end about
     * then check if there is a connected edevice now
     * by checking id the current [connectedNodeId] is not [null]
     * then we encode it into [ByteArray]
     * then we call the [client] which is from the Wearable Lib
     *  which takes some args to be able to send to the other end
     */
    override suspend fun sendOrQueueAction(
        action: MessageAction
    ): EmptyDataResult<MessaningError> {


        return connectedNodeId?.let { id ->
            try {


                val json = Json.encodeToString(action.toMessageActionDto())

                /**
                 * @param [id]= [id] is the id of the device connected to our end now
                 * @param  [pathId]= [BASE_BASE_MESSAGING_ACTION] is the path of the actions to be stored in to be able to add or get from it
                 * @param [messageByteArray] = [json] converted to byteArray
                 */
                /**
                 *then we await for the function to completely done then we return [AppResult.Done]
                 *
                 */
                client.sendMessage(
                    id,
                    BASE_BASE_MESSAGING_ACTION,
                    json.encodeToByteArray()
                ).await()
                AppResult.Done(Unit)
            } catch (e: ApiException) {

                AppResult.Error(
                    if (e.status.isInterrupted)
                        MessaningError.CONNECTION_INTERRUPTED else MessaningError.UNKNOWN
                )

            }
        } ?: run {
            messageQueue.add(action)
            AppResult.Error(MessaningError.DISCONNECTED)
        }
    }

    /**
     * when you connect to devices via Bluetooth
     * what happen is a shared folder  is created to  store  the data and commuinications between both
     * so we declared a path that will actions we tend to send and recieve data to be sotred in so we
     * can listen to stream of connections and if it starts with [BASE_BASE_MESSAGING_ACTION]
     * then we are sure it holds some actions of owr own app that we can convert it into
     * executable code the app can understand
     */
    companion object {
        private const val BASE_BASE_MESSAGING_ACTION = "runique/messaging_action"
    }
}