package dev.sublab.substrate

import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.hashers.DefaultHashersProvider
import dev.sublab.substrate.hashers.HashersProvider
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.modules.DefaultModuleRpcProvider
import dev.sublab.substrate.modules.ModuleRpcProvider
import dev.sublab.substrate.rpcClient.RpcClient
import dev.sublab.substrate.utils.JobWithTimeout
import dev.sublab.substrate.webSocketClient.WebSocketClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

typealias HexScaleCodec = ScaleCodec<String>

class SubstrateClient(
    url: String,
    settings: SubstrateClientSettings = SubstrateClientSettings.default(),
    private val codecProvider: ScaleCodecProvider = ScaleCodecProvider.default(),
    private val hashers: HashersProvider = DefaultHashersProvider(),
    val modules: ModuleRpcProvider = DefaultModuleRpcProvider(codecProvider, RpcClient(url), hashers),
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

    private suspend fun loadRuntime() = modules.stateRpc().getRuntimeMetadata()
    private fun getRuntime(): Flow<RuntimeMetadata> {
        runtimeMetadataUpdate.perform()
        return runtimeMetadata.filterNotNull()
    }

    val lookupService = SubstrateLookupService(getRuntime(), settings.namingPolicy)
    val constantsService = SubstrateConstantsService(codecProvider.byteArray, lookupService)
    val storageService = SubstrateStorageService(codecProvider.byteArray, lookupService, modules.stateRpc())
}