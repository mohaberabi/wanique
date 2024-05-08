package com.mohaberabi.core.connectivity.data

import com.google.android.gms.wearable.Node
import com.mohaberabi.core.connectivity.domain.DeviceNode


fun Node.toDeviceNode(): DeviceNode {
    return DeviceNode(
        id,
        displayName,
        isNearby
    )
}