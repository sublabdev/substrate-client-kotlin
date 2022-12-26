package dev.sublab.substrate.support

import dev.sublab.common.numerics.UInt32

internal interface Network {
    interface LocalRuntimeMetadataSnapshot {
        val path: String
        val magicNumber: UInt32
    }

    val rpcUrl: String
    val addressType: Int
    val localRuntimeMetadataSnapshot: LocalRuntimeMetadataSnapshot
}

internal class PolkadotNetwork: Network {
    override val rpcUrl = "rpc.polkadot.io"
    override val addressType = 0
    override val localRuntimeMetadataSnapshot = object : Network.LocalRuntimeMetadataSnapshot {
        override val path = "runtimes/polkadot-runtime"
        override val magicNumber = 1635018093U
    }
}

internal class KusamaNetwork: Network {
    override val rpcUrl = "kusama-rpc.polkadot.io"
    override val addressType = 2
    override val localRuntimeMetadataSnapshot = object : Network.LocalRuntimeMetadataSnapshot {
        override val path = "runtimes/kusama-runtime"
        override val magicNumber = 1635018093U
    }
}

internal class WestendNetwork: Network {
    override val rpcUrl = "westend-rpc.polkadot.io"
    override val addressType = 42
    override val localRuntimeMetadataSnapshot = object : Network.LocalRuntimeMetadataSnapshot {
        override val path = "runtimes/westend-runtime"
        override val magicNumber = 1635018093U
    }
}

internal fun allNetworks() = listOf(PolkadotNetwork(), KusamaNetwork(), WestendNetwork())