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

import dev.sublab.common.ByteArrayConvertible
import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorage
import dev.sublab.substrate.metadata.modules.storage.item.RuntimeModuleStorageItem
import dev.sublab.substrate.modules.state.StateRpc
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

/**
 * Substrate storage service
 */
class SubstrateStorageService(
    private val codec: ScaleCodec<ByteArray>,
    private val lookup: SubstrateLookupService,
    private val stateRpc: StateRpc
) {

    /**
     * Finds a storage item result, which is a wrapper over runtime module storage item and runtime module storage itself
     * by previously fetching the module
     * @param moduleName module's name to fetch
     * @param itemName storage item's name
     * @return A storage item result from a module
     */
    fun find(moduleName: String, itemName: String) = lookup.findStorageItem(moduleName, itemName)

    /**
     * Returns a storage item after getting a module first
     * @param moduleName module's name to fetch
     * @param itemName storage item's name
     * @param type a generic type T for a result
     *
     */
    fun <T: Any> fetch(moduleName: String, itemName: String, type: KClass<T>) = find(moduleName, itemName)
        .map { result ->
            result?.let { fetch(it.item, it.storage, type) }
        }

    /**
     * Fetches a storage item after getting a module first
     * @param module's name to fetch
     * @param itemName storage item's name
     * @param key a key to use for fetching a storage item
     * @param type a generic type T for a result
     */
    fun <T: Any> fetch(moduleName: String, itemName: String, key: ByteArrayConvertible, type: KClass<T>)
        = find(moduleName, itemName).map { result ->
            result?.let { fetch(it.item, key, it.storage, type) }
        }

    /**
     * Fetches a storage item after getting a module first
     * @param module's name to fetch
     * @param itemName storage item's name
     * @param keys keys to use for fetching a storage item
     * @param type a generic type T for a result
     */
    fun <T: Any> fetch(moduleName: String, itemName: String, keys: List<ByteArrayConvertible>, type: KClass<T>)
        = find(moduleName, itemName).map { result ->
            result?.let { fetch(it.item, keys, it.storage, type) }
        }

    /**
     * Fetches storage item from a specified storage
     * @param item an item to be hashed
     * @param storage a storage for which a storage hasher is created, which hashes the item
     */
    suspend fun <T: Any> fetch(item: RuntimeModuleStorageItem, storage: RuntimeModuleStorage, type: KClass<T>)
        = stateRpc.fetchStorageItem(item, storage, type)

    /**
     * Fetches storage item from a specified storage
     * @param item an item to be hashed
     * @param key a key to be used when hashing in a storage hasher
     * @param storage storage for which a storage hasher is created, which hashes the item
     */
    suspend fun <T: Any> fetch(
        item: RuntimeModuleStorageItem,
        key: ByteArrayConvertible,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    ) = stateRpc.fetchStorageItem(item, key, storage, type)

    /**
     * Fetches storage item from a specified storage
     * @param item an item to be hashed
     * @param keys key to be used when hashing in a storage hasher
     * @param storage storage for which a storage hasher is created, which hashes the item
     */
    suspend fun <T: Any> fetch(
        item: RuntimeModuleStorageItem,
        keys: List<ByteArrayConvertible>,
        storage: RuntimeModuleStorage, type: KClass<T>
    ) = stateRpc.fetchStorageItem(item, keys, storage, type)
}