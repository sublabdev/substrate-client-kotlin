package dev.sublab.substrate

import dev.sublab.hashing.utils.ByteArrayConvertible
import dev.sublab.scale.ScaleCodec
import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorage
import dev.sublab.substrate.metadata.modules.storage.item.RuntimeModuleStorageItem
import dev.sublab.substrate.modules.state.StateRpc
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

class SubstrateStorageService(
    private val codec: ScaleCodec<ByteArray>,
    private val lookup: SubstrateLookupService,
    private val stateRpc: StateRpc
) {

    fun find(moduleName: String, itemName: String) = lookup.findStorageItem(moduleName, itemName)

    fun <T: Any> fetch(moduleName: String, itemName: String, type: KClass<T>) = find(moduleName, itemName)
        .map { result ->
            result?.let { fetch(it.item, it.storage, type) }
        }

    fun <T: Any> fetch(moduleName: String, itemName: String, key: ByteArrayConvertible, type: KClass<T>)
        = find(moduleName, itemName).map { result ->
            result?.let { fetch(it.item, key, it.storage, type) }
        }

    fun <T: Any> fetch(moduleName: String, itemName: String, keys: List<ByteArrayConvertible>, type: KClass<T>)
        = find(moduleName, itemName).map { result ->
            result?.let { fetch(it.item, keys, it.storage, type) }
        }

    suspend fun <T: Any> fetch(item: RuntimeModuleStorageItem, storage: RuntimeModuleStorage, type: KClass<T>)
        = stateRpc.fetchStorageItem(item, storage, type)

    suspend fun <T: Any> fetch(
        item: RuntimeModuleStorageItem,
        key: ByteArrayConvertible,
        storage: RuntimeModuleStorage,
        type: KClass<T>
    ) = stateRpc.fetchStorageItem(item, key, storage, type)

    suspend fun <T: Any> fetch(
        item: RuntimeModuleStorageItem,
        keys: List<ByteArrayConvertible>,
        storage: RuntimeModuleStorage, type: KClass<T>
    ) = stateRpc.fetchStorageItem(item, keys, storage, type)
}