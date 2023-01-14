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

/**
 * Interface for getting Runtime metadata and fetching Storage Items
 */
interface StateRpc {
    /**
     * Gets runtime metadata
     */
    suspend fun getRuntimeMetadata(): RuntimeMetadata?

    /**
     * Fetches storage item
     * @param item an item to be hashed to get a key which can be used as `RpcRequest`'s parameters
     * @param storage storage for which a storage hasher is created, which hashes the item
     * @return  A storage item
     */
    suspend fun <T: Any> fetchStorageItem(
        item: RuntimeModuleStorageItem,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    ): T?

    /**
     * Fetches storage item
     * @param item an item to be hashed to get a key which can be used as `RpcRequest`'s parameters
     * @param key A key to be used when hashing in a storage hasher.
     * @param storage storage for which a storage hasher is created, which hashes the item
     * @return  A storage item
     */
    suspend fun <T: Any> fetchStorageItem(
        item: RuntimeModuleStorageItem,
        key: ByteArrayConvertible,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    ): T?

    /**
     * Fetches storage item
     * @param item an item to be hashed to get a key which can be used as `RpcRequest`'s parameters
     * @param keys Keys to be used when hashing in a storage hasher.
     * @param storage storage for which a storage hasher is created, which hashes the item
     * @return  A storage item
     */
    suspend fun <T: Any> fetchStorageItem(
        item: RuntimeModuleStorageItem,
        keys: List<ByteArrayConvertible>,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    ): T?
}

/**
 * State RPC client which handles fetching storage item and runtime metadata
 */
class StateRpcClient(
    private val codec: HexScaleCodec,
    private val rpcClient: RpcClient,
    private val hashersProvider: HashersProvider
): StateRpc {
    override suspend fun getRuntimeMetadata() = rpcClient.sendRequest<Unit, String> {
        method = "state_getMetadata"
        responseType = String::class
    }?.let {
        codec.fromScale(it, RuntimeMetadata::class)
    }

    /**
     * Fetches a storage item using its key
     */
    private suspend fun <T: Any> fetchStorageItem(
        key: ByteArray,
        type: KClass<T>
    ) = rpcClient.sendRequest<String, String> {
        method = "state_getStorage"
        responseType = String::class
        paramsType = String::class
        params = listOf(key.hex.encode(true))
    }?.let {
        codec.fromScale(it, type)
    }

    /**
     * Fetches a storage item
     */
    override suspend fun <T : Any> fetchStorageItem(
        item: RuntimeModuleStorageItem,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    ) = fetchStorageItem(hashersProvider.getStorageHasher(storage).hash(item), type)

    /**
     * Fetches a storage item
     */
    override suspend fun <T : Any> fetchStorageItem(
        item: RuntimeModuleStorageItem,
        key: ByteArrayConvertible,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    ) = fetchStorageItem(hashersProvider.getStorageHasher(storage).hash(item, listOf(key)), type)

    /**
     * Fetches a storage item
     */
    override suspend fun <T : Any> fetchStorageItem(
        item: RuntimeModuleStorageItem,
        keys: List<ByteArrayConvertible>,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    )  = fetchStorageItem(hashersProvider.getStorageHasher(storage).hash(item, keys), type)
}