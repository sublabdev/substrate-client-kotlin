package dev.sublab.substrate.hashers

import dev.sublab.common.ByteArrayConvertible
import dev.sublab.hashing.*
import dev.sublab.hashing.hashers.*
import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorage
import dev.sublab.substrate.metadata.modules.storage.RuntimeModuleStorageHasher
import dev.sublab.substrate.metadata.modules.storage.item.RuntimeModuleStorageItem
import dev.sublab.substrate.metadata.modules.storage.item.type.RuntimeModuleStorageItemType

/**
 * Interface for providing a hashing functionality
 */
interface StorageHashing {
    /**
     * Hashes provided storage item. The hashing can be either plain or key-map
     * @param storageItem a storage item to hash
     * @param keys keys for hashing by key-mapping
     * @return A hashed `ByteArray`
     */
    fun hash(storageItem: RuntimeModuleStorageItem, keys: List<ByteArrayConvertible> = listOf()): ByteArray
}

/**
 * Handles storage hashing
 */
class StorageHasher(
    private val storage: RuntimeModuleStorage
): StorageHashing {
    private fun hashPlainKey(storageItem: RuntimeModuleStorageItem)
        = storage.prefix.hashing.xx128() + storageItem.name.hashing.xx128()

    private fun hash(key: ByteArray, hasher: RuntimeModuleStorageHasher): ByteArray = when (hasher) {
        RuntimeModuleStorageHasher.Blake128 -> key.hashing.blake2b_128()
        RuntimeModuleStorageHasher.Blake256 -> key.hashing.blake2b_256()
        RuntimeModuleStorageHasher.Blake128Concat -> key.hashing.blake2b_128() + key
        RuntimeModuleStorageHasher.Twox128 -> key.hashing.xx128()
        RuntimeModuleStorageHasher.Twox256 -> key.hashing.xx256()
        RuntimeModuleStorageHasher.Twox64Concat -> key.hashing.xx64() + key
        RuntimeModuleStorageHasher.Identity -> key
    }

    private fun hashMapKey(
        storageItem: RuntimeModuleStorageItem,
        keys: List<ByteArrayConvertible>,
        hashers: List<RuntimeModuleStorageHasher>
    ) = hashers
        .mapIndexed { index, hasher -> hash(keys[index].toByteArray(), hasher) }
        .fold(hashPlainKey(storageItem)) { result, hash -> result + hash }

    override fun hash(storageItem: RuntimeModuleStorageItem, keys: List<ByteArrayConvertible>) = when (storageItem.type) {
        is RuntimeModuleStorageItemType.Plain -> hashPlainKey(storageItem)
        is RuntimeModuleStorageItemType.Map -> {
            check(keys.size == storageItem.type.value.hashers.size) { "Keys count should be equal to hashers count" }
            hashMapKey(storageItem, keys, storageItem.type.value.hashers)
        }
    }
}