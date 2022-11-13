package dev.sublab.substrate.support

interface Network {
    interface LocalRuntimeMetadataSnapshot {
        val path: String
        val magicNumber: UInt
    }

    val rpcUrl: String
    val localRuntimeMetadataSnapshot: LocalRuntimeMetadataSnapshot
}

class PolkadotNetwork: Network {
    class Snapshot: Network.LocalRuntimeMetadataSnapshot {
        override val path = "runtimes/polkadot-runtime"
        override val magicNumber = 1635018093U
    }

    override val rpcUrl = "rpc.polkadot.io"
    override val localRuntimeMetadataSnapshot = Snapshot()
}

class KusamaNetwork: Network {
    class Snapshot: Network.LocalRuntimeMetadataSnapshot {
        override val path = "runtimes/kusama-runtime"
        override val magicNumber = 1635018093U
    }

    override val rpcUrl = "kusama-rpc.polkadot.io"
    override val localRuntimeMetadataSnapshot = Snapshot()
}

class WestendNetwork: Network {
    class Snapshot: Network.LocalRuntimeMetadataSnapshot {
        override val path = "runtimes/westend-runtime"
        override val magicNumber = 1635018093U
    }

    override val rpcUrl = "westend-rpc.polkadot.io"
    override val localRuntimeMetadataSnapshot = Snapshot()
}

fun allNetworks() = listOf(PolkadotNetwork(), KusamaNetwork(), WestendNetwork())