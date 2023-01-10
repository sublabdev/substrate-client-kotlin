package dev.sublab.substrate.support

import dev.sublab.common.numerics.UInt32
import dev.sublab.substrate.SubstrateClient
import dev.sublab.substrate.SubstrateClientSettings
import dev.sublab.substrate.rpcClient.RpcClient

internal abstract class Network {
    interface LocalRuntimeMetadataSnapshot {
        val path: String
        val magicNumber: UInt32
    }

    abstract val url: String
    abstract val addressType: Int
    abstract val genesisHash: String
    abstract val localRuntimeMetadataSnapshot: LocalRuntimeMetadataSnapshot

    fun makeClient() = makeClient(this)
    fun makeRpcClient() = makeRpcClient(this)
}

internal class PolkadotNetwork: Network() {
    override val url = "polkadot.api.onfinality.io"
    override val addressType = 0
    override val genesisHash = "0x91b171bb158e2d3848fa23a9f1c25182fb8e20313b2c1eb49219da7a70ce90c3"
    override val localRuntimeMetadataSnapshot = object : LocalRuntimeMetadataSnapshot {
        override val path = "runtimes/polkadot-runtime"
        override val magicNumber = 1635018093U
    }
}

internal class KusamaNetwork: Network() {
    override val url = "kusama.api.onfinality.io"
    override val addressType = 2
    override val genesisHash = "0xb0a8d493285c2df73290dfb7e61f870f17b41801197a149ca93654499ea3dafe"
    override val localRuntimeMetadataSnapshot = object : LocalRuntimeMetadataSnapshot {
        override val path = "runtimes/kusama-runtime"
        override val magicNumber = 1635018093U
    }
}

internal class WestendNetwork: Network() {
    override val url = "westend.api.onfinality.io"
    override val addressType = 42
    override val genesisHash = "0xe143f23803ac50e8f6f8e62695d1ce9e4e1d68aa36c1cd2cfd15340213f3423e"
    override val localRuntimeMetadataSnapshot = object : LocalRuntimeMetadataSnapshot {
        override val path = "runtimes/westend-runtime"
        override val magicNumber = 1635018093U
    }
}

internal fun allNetworks() = listOf(
    PolkadotNetwork(),
    KusamaNetwork(),
    WestendNetwork()
)

private const val onFinalityKey = "4d709852-8a96-4e7e-962d-0efe46d5a44c"

private fun makeSettings() = SubstrateClientSettings.default().let { default ->
    SubstrateClientSettings(
        rpcPath = "rpc",
        rpcParams = mapOf("apikey" to onFinalityKey),
        webSocketSecure = true,
        webSocketPath = "ws",
        webSocketParams = mapOf("apikey" to onFinalityKey),
        webSocketPort = null,
        runtimeMetadataUpdateTimeoutMs = default.runtimeMetadataUpdateTimeoutMs,
        namingPolicy = default.namingPolicy,
        objectStorageFactory = default.objectStorageFactory
    )
}

private fun makeClient(network: Network) = SubstrateClient(
    url = network.url,
    settings = makeSettings()
)

private fun makeRpcClient(network: Network) = RpcClient(
    host = network.url,
    path = "rpc",
    params = mapOf("apikey" to onFinalityKey)
)