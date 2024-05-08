package com.mohaberabi.core.connectivity.data

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Wearable
import com.mohaberabi.core.connectivity.domain.AppsIds
import com.mohaberabi.core.connectivity.domain.DeviceNode
import com.mohaberabi.core.connectivity.domain.DeviceType
import com.mohaberabi.core.connectivity.domain.NodeDiscovery
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class WearNodeDescovery(
    private val context: Context,
) : NodeDiscovery {
    private val capabilityClient = Wearable.getCapabilityClient(context)
    override fun observeConnectedDevice(
        localDevice:
        DeviceType
    ): Flow<Set<DeviceNode>> {
        return callbackFlow {


            /**
             * [remoteCable] is just an identifer to the capiblity info client
             * now if we are on a phone we need to listen to wearable devices and vise versa
             * that was also provided  in the XML Values Resources
             */
            val remoteCable = when (localDevice) {
                DeviceType.PHONE -> AppsIds.WEAR_APP
                DeviceType.WATCH -> AppsIds.PHONE_APP
            }

            try {

                /**
                 *now calling the [getCapability] from [capabilityClient] passing the [remoteCable]
                 *also [CapabilityClient.FILTER_REACHABLE] means that only filter the reachable devices only not all paired
                 * we need only the nearest we can connect to at current time instance
                 * then await for the Coroutine to be done to return the info
                 */
                val capability =
                    capabilityClient.getCapability(
                        remoteCable,
                        CapabilityClient.FILTER_REACHABLE
                    ).await()

                /**
                 * get the current connected device from the client wich
                 * returns  a [List] of [Node]
                 * then we map each [Node] to our [DeviceNode] then conver into set to return it
                 */
                val connectedDevices = capability.nodes.map { it.toDeviceNode() }.toSet()
                send(connectedDevices)

            } catch (e: ApiException) {

                awaitClose()
                return@callbackFlow
            }

            /**
             * firing a listener to listen for changes of nodes
             */
            val listener: (CapabilityInfo) -> Unit = {
                trySend(it.nodes.map { info -> info.toDeviceNode() }.toSet())
            }

            capabilityClient.addListener(listener, remoteCable)
            awaitClose {
                capabilityClient.removeListener(listener)
            }
        }
    }
}