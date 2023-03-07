/**
 *
 * Copyright 2023 SUBSTRATE LABORATORY LLC <info@sublab.dev>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package dev.sublab.substrate

import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.hashers.DefaultHashersProvider
import dev.sublab.substrate.hashers.HashersProvider
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.modules.DefaultModuleProvider
import dev.sublab.substrate.modules.InternalModuleProvider
import dev.sublab.substrate.modules.ModuleProvider
import dev.sublab.substrate.rpcClient.RpcClient
import dev.sublab.substrate.utils.JobWithTimeout
import dev.sublab.substrate.webSocketClient.WebSocket
import dev.sublab.substrate.webSocketClient.WebSocketClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

typealias HexScaleCodec = ScaleCodec<String>

/**
 * Substrate client which holds substrate lookup service; constants service and storage service.
 * Is the entering point for using those services.
 */
class SubstrateClient(
    url: String,
    settings: SubstrateClientSettings = SubstrateClientSettings.default(),
    private val codecProvider: ScaleCodecProvider = ScaleCodecProvider.default(),
    private val hashers: HashersProvider = DefaultHashersProvider(),
    private val moduleProvider: InternalModuleProvider = DefaultModuleProvider(
        codecProvider = codecProvider,
        rpc = RpcClient(url, settings.rpcPath, settings.rpcParams),
        hashersProvider = hashers
    )
) {
    // For testing purposes
    internal companion object

    val modules: ModuleProvider get() = moduleProvider

    val webSocket: WebSocket = WebSocketClient(
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

    private suspend fun loadRuntime() = modules.state.getRuntimeMetadata()
    internal fun getRuntime(): Flow<RuntimeMetadata> {
        runtimeMetadataUpdate.perform()
        return runtimeMetadata.filterNotNull()
    }

    val lookup: SubstrateLookup
    val constants: SubstrateConstants
    val storage: SubstrateStorage
    val extrinsics: SubstrateExtrinsics

    init {
        // Supply dependencies
        moduleProvider.workingWithClient(this)
        codecProvider.applyRuntimeMetadata(getRuntime())

        // Init after dependencies set
        lookup = SubstrateLookupService(getRuntime(), settings.namingPolicy, settings.lookupPolicy)
        constants = SubstrateConstantsService(codecProvider.byteArray, lookup)
        storage = SubstrateStorageService(codecProvider.byteArray, lookup, modules.state)
        extrinsics = SubstrateExtrinsicsService(
            runtimeMetadata = getRuntime(),
            systemRpc = modules.system,
            chainRpc = modules.chain,
            codec = codecProvider.byteArray,
            lookup = lookup,
            namingPolicy = settings.namingPolicy,
            policy = settings.extrinsicsPolicy
        )
    }
}