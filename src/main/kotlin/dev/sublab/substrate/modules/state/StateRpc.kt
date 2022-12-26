package dev.sublab.substrate.modules.state

import dev.sublab.common.ByteArrayConvertible
import dev.sublab.hex.hex
import dev.sublab.substrate.HexScaleCodec
import dev.sublab.substrate.hashers.HashersProvider
import dev.sublab.substrate.metadata.RuntimeMetadata
import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorage
import dev.sublab.substrate.metadata.modules.storage.item.RuntimeModuleStorageItem
import dev.sublab.substrate.rpcClient.RpcClient
import kotlin.reflect.KClass

interface StateRpc {
    suspend fun getRuntimeMetadata(): RuntimeMetadata

    suspend fun <T: Any> fetchStorageItem(
        item: RuntimeModuleStorageItem,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    ): T

    suspend fun <T: Any> fetchStorageItem(
        item: RuntimeModuleStorageItem,
        key: ByteArrayConvertible,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    ): T

    suspend fun <T: Any> fetchStorageItem(
        item: RuntimeModuleStorageItem,
        keys: List<ByteArrayConvertible>,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    ): T
}

class StateRpcClient(
    private val codec: HexScaleCodec,
    private val rpcClient: RpcClient,
    private val hashersProvider: HashersProvider
): StateRpc {
    override suspend fun getRuntimeMetadata() = rpcClient.sendRequest<Unit, String> {
        method = "state_getMetadata"
        responseType = String::class
    }.let {
        codec.fromScale(it, RuntimeMetadata::class)
    }

    private suspend fun <T: Any> fetchStorageItem(
        key: ByteArray,
        type: KClass<T>
    ) = rpcClient.sendRequest<String, String> {
        method = "state_getStorage"
        responseType = String::class
        paramsType = String::class
        params = listOf(key.hex.encode(true))
    }.let {
        codec.fromScale(it, type)
    }

    override suspend fun <T : Any> fetchStorageItem(
        item: RuntimeModuleStorageItem,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    ) = fetchStorageItem(hashersProvider.getStorageHasher(storage).hash(item), type)

    override suspend fun <T : Any> fetchStorageItem(
        item: RuntimeModuleStorageItem,
        key: ByteArrayConvertible,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    ) = fetchStorageItem(hashersProvider.getStorageHasher(storage).hash(item, listOf(key)), type)

    override suspend fun <T : Any> fetchStorageItem(
        item: RuntimeModuleStorageItem,
        keys: List<ByteArrayConvertible>,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    )  = fetchStorageItem(hashersProvider.getStorageHasher(storage).hash(item, keys), type)
}