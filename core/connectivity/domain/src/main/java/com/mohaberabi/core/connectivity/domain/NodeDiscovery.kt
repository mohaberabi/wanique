package com.mohaberabi.core.connectivity.domain

import kotlinx.coroutines.flow.Flow


/**
 * observe any nodes that can be connected  to [mobile , watch , tablets,....etc]
 */
interface NodeDiscovery {


    fun observeConnectedDevice(
        localDevice:
        DeviceType
    ): Flow<Set<DeviceNode>>


}


