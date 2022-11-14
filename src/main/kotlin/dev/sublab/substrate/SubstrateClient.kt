package dev.sublab.substrate

import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.rpcClient.DefaultModuleRpcProvider
import dev.sublab.substrate.rpcClient.ModuleRpcProvider
import dev.sublab.substrate.rpcClient.RpcClient
import dev.sublab.substrate.utils.JobWithTimeout
import dev.sublab.substrate.webSocketClient.WebSocketClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

class SubstrateClient(
    url: String,
    private val settings: SubstrateClientSettings = SubstrateClientSettings.default(),
    internal val codec: ScaleCodec<ByteArray> = ScaleCodec.default(),
    val modules: ModuleRpcProvider = DefaultModuleRpcProvider(codec, RpcClient(url))
) {
    private val webSocketClient = WebSocketClient(
        host = url,
        path = settings.webSocketPath,
        port = settings.webSocketPort
    )

    private val runtimeMetadata: MutableStateFlow<RuntimeMetadata?> = settings.objectStorageFactory.make()
    private val runtimeMetadataUpdate = JobWithTimeout(timeoutMs = settings.runtimeMetadataUpdateTimeoutMs) {
        runtimeMetadata.value = loadRuntime()
    }

    internal suspend fun loadRuntime() = modules.stateRpc().getRuntimeMetadata()
    internal fun getRuntime(): Flow<RuntimeMetadata> {
        runtimeMetadataUpdate.perform()
        return runtimeMetadata.filterNotNull()
    }

    fun getConstantsService() = SubstrateConstantsService(codec, getRuntime(), settings.namingPolicy)
}