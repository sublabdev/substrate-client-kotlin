package dev.sublab.substrate

import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.hashers.DefaultHashersProvider
import dev.sublab.substrate.hashers.HashersProvider
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.modules.DefaultModuleRpcProvider
import dev.sublab.substrate.modules.InternalModuleRpcProvider
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
    internal val codecProvider: ScaleCodecProvider = ScaleCodecProvider.default(),
    private val hashers: HashersProvider = DefaultHashersProvider(),
    private val moduleRpcProvider: InternalModuleRpcProvider = DefaultModuleRpcProvider(
        codecProvider = codecProvider,
        rpcClient = RpcClient(url, settings.rpcPath, settings.rpcParams),
        hashersProvider = hashers
    )
) {
    // For testing purposes
    internal companion object

    val modules: ModuleRpcProvider get() = moduleRpcProvider

    val webSocket = WebSocketClient(
        secure = settings.webSocketSecure,
        host = url,
        path = settings.webSocketPath,
        params = settings.webSocketParams,
        port = settings.webSocketPort
    )

    private val runtimeMetadata: MutableStateFlow<RuntimeMetadata?> = settings.objectStorageFactory.make()
    private val runtimeMetadataUpdate = JobWithTimeout(timeoutMs = settings.runtimeMetadataUpdateTimeoutMs) {
        runtimeMetadata.value = loadRuntime()
    }

    private suspend fun loadRuntime() = modules.stateRpc().getRuntimeMetadata()
    internal fun getRuntime(): Flow<RuntimeMetadata> {
        runtimeMetadataUpdate.perform()
        return runtimeMetadata.filterNotNull()
    }

    val lookup: SubstrateLookupService
    val constants: SubstrateConstantsService
    val storage: SubstrateStorageService
    val extrinsics: SubstrateExtrinsicsService

    init {
        // Supply dependencies
        moduleRpcProvider.workingWithClient(this)
        codecProvider.applyRuntimeMetadata(getRuntime())

        // Init after dependencies set
        lookup = SubstrateLookupService(getRuntime(), settings.namingPolicy)
        constants = SubstrateConstantsService(codecProvider.byteArray, lookup)
        storage = SubstrateStorageService(codecProvider.byteArray, lookup, modules.stateRpc())
        extrinsics = SubstrateExtrinsicsService(
            runtimeMetadata = getRuntime(),
            systemRpc = modules.systemRpc(),
            chainRpc = modules.chainRpc(),
            codec = codecProvider.byteArray,
            lookup = lookup,
            namingPolicy = settings.namingPolicy
        )
    }
}