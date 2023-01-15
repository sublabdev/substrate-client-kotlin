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
     * Returns runtime metadata
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
     * @param key a key used in a parameters when fetching a storage item
     * @param type a generic type [T]
     * @return A storage item
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
     * @param item a runtime module storage item
     * @param storage a runtime module storage
     * @param type a generic type [T]
     * @return A storage item
     */
    override suspend fun <T : Any> fetchStorageItem(
        item: RuntimeModuleStorageItem,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    ) = fetchStorageItem(hashersProvider.getStorageHasher(storage).hash(item), type)

    /**
     * Fetches a storage item
     * @param item a runtime module storage item
     * @param key a [ByteArrayConvertible] used while hashing
     * @param storage a runtime module storage
     * @param type a generic type [T]
     * @return A storage item
     */
    override suspend fun <T : Any> fetchStorageItem(
        item: RuntimeModuleStorageItem,
        key: ByteArrayConvertible,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    ) = fetchStorageItem(hashersProvider.getStorageHasher(storage).hash(item, listOf(key)), type)

    /**
     * Fetches a storage item
     * @param item a runtime module storage item
     * @param keys a list of [ByteArrayConvertible] used while hashing
     * @param storage a runtime module storage
     * @param type a generic type [T]
     * @return A storage item
     */
    override suspend fun <T : Any> fetchStorageItem(
        item: RuntimeModuleStorageItem,
        keys: List<ByteArrayConvertible>,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    )  = fetchStorageItem(hashersProvider.getStorageHasher(storage).hash(item, keys), type)
}