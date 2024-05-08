package com.mohaberabi.core.connectivity.di

import com.mohaberabi.core.connectivity.data.WearNodeDescovery
import com.mohaberabi.core.connectivity.data.messanging.WearMessaningClient
import com.mohaberabi.core.connectivity.domain.NodeDiscovery
import com.mohaberabi.core.connectivity.domain.messanging.MessangingClient
import com.mohaberabi.core.connectivity.domain.messanging.MessaningError
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val coreConnectivityDataModule = module {


    singleOf(::WearNodeDescovery).bind<NodeDiscovery>()
    singleOf(::WearMessaningClient).bind<MessangingClient>()


}